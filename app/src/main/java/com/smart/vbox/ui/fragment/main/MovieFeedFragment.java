package com.smart.vbox.ui.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.grpc.vbox.VBox;
import com.grpc.vbox.VBox_ServiceGrpc;
import com.smart.vbox.R;
import com.smart.vbox.support.GrpcManager;
import com.smart.vbox.support.adapter.recyclerview.VideoFeedAdapter;
import com.smart.vbox.support.utils.GlobalUtils;
import com.smart.vbox.support.utils.ViewUtils;
import com.smart.vbox.ui.fragment.BaseFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.vov.vitamio.Vitamio;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author lhq
 *         created at 2015/10/24 10:32
 */
public class MovieFeedFragment extends BaseFragment implements VideoFeedAdapter.OnFeedItemClickListener {
    public static int PAGE_COUNT = 3;

    private boolean mIsPrepared;
    private boolean mIsVitamioReady;
    private int lastVisibleItem;
    private boolean isFeedEnd;
    private boolean isLoading;

    private VideoFeedAdapter feedAdapter;
    private boolean pendingIntroAnimation;

    @Bind(R.id.rv_video_feed)
    RecyclerView rvFeed;

    @Bind(R.id.swipe_fresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_feed, container, false);
        ButterKnife.bind(this, view);

        //检查播放器有没有初始化
        if (!Vitamio.isInitialized(getActivity())) {
            mIsVitamioReady = Vitamio.initialize(getActivity(), this.getResources().getIdentifier("libarm", "raw", getActivity().getPackageName()));
        } else {
            mIsVitamioReady = true;
        }
        setupFeed();

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        } else {
            feedAdapter.updateItems(false);
        }
        return view;
    }

    private void setupFeed() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);

        feedAdapter = new VideoFeedAdapter(getActivity());
        feedAdapter.setOnFeedItemClickListener(this);
        rvFeed.setAdapter(feedAdapter);

        ViewUtils.setSwipeRefreshLayoutColor(mSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lazyLoad();
                isFeedEnd = false;
                mSwipeRefreshLayout.setEnabled(false);
            }
        });

        //RecyclerView滑动监听
        rvFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLoading || isFeedEnd) {
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == feedAdapter.getItemCount()) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

        mIsPrepared = true;
        lazyLoad();
    }

    private void loadMore() {
        Observable.create(new Observable.OnSubscribe<List<VBox.VAbstractVideoObject>>() {
            @Override
            public void call(Subscriber<? super List<VBox.VAbstractVideoObject>> subscriber) {
                try {
                    isLoading = true;
                    VBox_ServiceGrpc.VBox_ServiceBlockingStub vBoxStub = GrpcManager.getInstance().getStub();
                    VBox.ADBrowseShortVideoReq.Builder builder = VBox.ADBrowseShortVideoReq.newBuilder();
                    // 设置已请求的Video ID
                    List<VBox.VAbstractVideoObject> videoList = feedAdapter.getVideoFeed();
                    if (videoList != null && !videoList.isEmpty()) {
                        for (VBox.VAbstractVideoObject obj : videoList) {
                            builder.addCachedVideoID(obj.getVideoID());
                        }
                    }
                    VBox.ADBrowseShortVideoReq req = builder.setTerminalMac(GlobalUtils.getLocalMacAddress(getActivity())).setReqVideoNum(PAGE_COUNT).build();
                    VBox.ADBrowseShortVideoRsp res = vBoxStub.browseShortVideo(req);

                    subscriber.onNext(res.getShortVideopageList());

                } catch (SecurityException | UncheckedExecutionException e) {
                    e.printStackTrace();
                    subscriber.onNext(null);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<VBox.VAbstractVideoObject>>() {
                    @Override
                    public void call(List<VBox.VAbstractVideoObject> groupList) {
                        isLoading = false;
                        // 进行显示
                        if (groupList == null || groupList.isEmpty()) {
                            isFeedEnd = true;
                            Toast.makeText(getActivity(), "没有更多啦~", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        feedAdapter.addVideoFeed(groupList);
                    }
                });
    }

    @Override
    protected void lazyLoad() {

        Observable.create(new Observable.OnSubscribe<List<VBox.VAbstractVideoObject>>() {
            @Override
            public void call(Subscriber<? super List<VBox.VAbstractVideoObject>> subscriber) {
                try {
                    VBox_ServiceGrpc.VBox_ServiceBlockingStub vBoxStub = GrpcManager.getInstance().getStub();
                    VBox.ADBrowseShortVideoReq req = VBox.ADBrowseShortVideoReq.newBuilder().setTerminalMac(GlobalUtils.getLocalMacAddress(getActivity())).setReqVideoNum(PAGE_COUNT).build();
                    VBox.ADBrowseShortVideoRsp res = vBoxStub.browseShortVideo(req);

                    subscriber.onNext(res.getShortVideopageList());

                    Log.i("xixi", "" + res.getShortVideopageList().size());

//                  测试数据
//                    VBox.VAbstractVideoObject ob1 = VBox.VAbstractVideoObject.newBuilder().setVideoTitle("hua qian gu").setVideoID(1).setVideoPosterUrl("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo_top_ca79a146.png").build();
//                    VBox.VAbstractVideoObject ob2 = VBox.VAbstractVideoObject.newBuilder().setVideoTitle("lang ya bang").setVideoID(2).setVideoPosterUrl("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo_top_ca79a146.png").build();
//                    VBox.ADBrowseShortVideoRsp adr = VBox.ADBrowseShortVideoRsp.newBuilder().addShortVideopage(0, ob1).addShortVideopage(1, ob2).build();
//                    Log.i("xixi", "Res : " + adr.getShortVideopageList().size());
//                    subscriber.onNext(adr.getShortVideopageList());

                } catch (SecurityException | UncheckedExecutionException e) {
                    e.printStackTrace();
                    subscriber.onNext(null);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<VBox.VAbstractVideoObject>>() {
                    @Override
                    public void call(List<VBox.VAbstractVideoObject> groupList) {
                        // 进行显示
                        if (groupList == null || groupList.isEmpty()) {
                            isFeedEnd = true;
                            return;
                        }
                        feedAdapter.setVideoFeed(groupList);
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mSwipeRefreshLayout.setEnabled(true);
                        }
                        /* shutdownChannel(); */
                    }
                });
    }

    public void onCommentsClick(View v, int position) {
    }

    public void onProfileClick(View v) {
    }

}

