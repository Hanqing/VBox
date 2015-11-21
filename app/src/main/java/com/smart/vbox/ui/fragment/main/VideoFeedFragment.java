package com.smart.vbox.ui.fragment.main;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.grpc.vbox.VBox;
import com.grpc.vbox.VBox_ServiceGrpc;
import com.smart.vbox.R;
import com.smart.vbox.support.adapter.recyclerview.VideoFeedAdapter;
import com.smart.vbox.support.utils.ViewUtils;
import com.smart.vbox.ui.fragment.BaseFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.grpc.ChannelImpl;
import io.grpc.transport.okhttp.OkHttpChannelBuilder;
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
public class VideoFeedFragment extends BaseFragment implements VideoFeedAdapter.OnFeedItemClickListener {


    public static String HOST = "172.24.1.208";
    public static int PORT = 8202;
    public static int PAGE_COUNT = 3;

    private ChannelImpl mChannel;
    private boolean mIsPrepared;
    private boolean mIsVitamioReady;

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity()) {
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
                mSwipeRefreshLayout.setEnabled(false);
            }
        });

        mIsPrepared = true;
        lazyLoad();
    }

    public String getLocalMacAddress() {

        WifiManager wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        WifiInfo info = wifi.getConnectionInfo();

        return info.getMacAddress();

    }

    @Override
    protected void lazyLoad() {

        Observable.create(new Observable.OnSubscribe<List<VBox.VAbstractVideoObject>>() {
            @Override
            public void call(Subscriber<? super List<VBox.VAbstractVideoObject>> subscriber) {
                try {
                    if (mChannel == null)
                        mChannel = OkHttpChannelBuilder.forAddress(HOST, PORT).build();

                    VBox_ServiceGrpc.VBox_ServiceBlockingStub vBoxStub = VBox_ServiceGrpc.newBlockingStub(mChannel);
                    VBox.ADBrowseShortVideoReq req = VBox.ADBrowseShortVideoReq.newBuilder().setTerminalMac(getLocalMacAddress()).setReqVideoNum(10).build();
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
