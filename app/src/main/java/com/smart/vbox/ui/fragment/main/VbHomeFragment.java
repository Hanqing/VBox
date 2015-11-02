package com.smart.vbox.ui.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.grpc.xbox.vBoxGrpc;
import com.grpc.xbox.xbox;
import com.smart.vbox.R;
import com.smart.vbox.support.adapter.recyclerview.VbRecommendRvAdapter;
import com.smart.vbox.support.utils.ViewUtils;
import com.smart.vbox.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.grpc.ChannelImpl;
import io.grpc.transport.okhttp.OkHttpChannelBuilder;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 
 * @author lhq
 * created at 2015/10/24 10:32
 */
public class VbHomeFragment extends BaseFragment {

    public static String mHost = " ";
    public static int mPort = 0;
    private ChannelImpl mChannel;
    private boolean mIsPrepared;
    private VbRecommendRvAdapter mAdapter;

    @Bind(R.id.ac_fragment_recommend_recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.swipe_fresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ac_fragment_main_recommend, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                if (position % 5 == 0) {
                    return 2;
                }
                return 1;
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new VbRecommendRvAdapter.MyDecoration());
        mAdapter = new VbRecommendRvAdapter();
        //解决viewpager里滑动导致swipeReFreshLayout的出现
        mAdapter.setSwipeRefreshLayout(mSwipeRefreshLayout);
        mAdapter.setOnClickListener(new VbRecommendRvAdapter.OnClickListener() {
            @Override
            public void onClick(View view, String partitionType, String contentId) {
                if (partitionType != null) {
                    //启动分区页面
//                    VbPartitionVbtivity.startVbtivity(getVbtivity(), partitionType);
                } else if (contentId != null) {
                    //启动视频信息页面
//                    VbContentVbtivity.startVbtivity(getVbtivity(), contentId);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        ViewUtils.setSwipeRefreshLayoutColor(mSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGrpcResult();
                mSwipeRefreshLayout.setEnabled(false);
            }
        });

        mIsPrepared = true;
        lazyLoad();

        return view;
    }

    @Override
    protected void lazyLoad() {
        if (!mIsPrepared || !isVisible) {
            return;
        } else {
            if (mAdapter.getHomePageGroup() == null) {
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        getGrpcResult();
                        mSwipeRefreshLayout.setRefreshing(true);
                        mSwipeRefreshLayout.setEnabled(false);
                    }
                });
            }
        }

    }

    private void getGrpcResult() {
        // ------------網絡請求 ---------
        Observable.create(new Observable.OnSubscribe<List<xbox.vObjectGroup>>() {
            @Override
            public void call(Subscriber<? super List<xbox.vObjectGroup>> subscriber) {
                try {
                    if (mChannel == null)
                        mChannel = OkHttpChannelBuilder.forAddress(mHost, mPort).build();
                    vBoxGrpc.vBoxBlockingStub vBoxStub = vBoxGrpc.newBlockingStub(mChannel);
                    xbox.ADBrowseHomepageReq req = xbox.ADBrowseHomepageReq.newBuilder().build();
                    xbox.ADBrowseHomepageRsp res = vBoxStub.browseHomepage(req);

                    subscriber.onNext(res.getHomepageGroupList());
                } catch (SecurityException | UncheckedExecutionException e) {
                    e.printStackTrace();
                    subscriber.onNext(null);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<xbox.vObjectGroup>>() {
                    @Override
                    public void call(List<xbox.vObjectGroup> groupList) {
                        // 进行显示
                        mAdapter.onHomePageResult(groupList);
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mSwipeRefreshLayout.setEnabled(true);
                        }
                        /* shutdownChannel(); */ }
                });
//        VbRecommend = RetrofitConfig.getVbRecommend();
//
//         if (TextUtils.equals(httpGetType, VbString.HOT)) {
//            //首页热门焦点
//            Call<VbReHot> call = VbRecommend.onVbReHotResult(VbApi.getVbReHotUrl
//                    (VbString.PAGE_NO_NUM_1));
//            call.enqueue(new CallbVbk<VbReHot>() {
//                @Override
//                public void onResponse(Response<VbReHot> response) {
//                    VbReHot VbReHot = response.body();
//                    if (VbReHot != null
//                            && getVbtivity() != null
//                            && !getVbtivity().isDestroyed()
//                            && !getVbtivity().isFinishing()) {
//                        mAdapter.onVbReHotResult(VbReHot);
//                        if (mSwipeRefreshLayout != null) {
//                            mSwipeRefreshLayout.setRefreshing(false);
//                            mSwipeRefreshLayout.setEnabled(true);
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//                    if (getVbtivity() != null
//                            && !getVbtivity().isDestroyed()
//                            && !getVbtivity().isFinishing()) {
//                        GlobalUtils.showToastShort(MyApplication.getsInstance().getApplicationContext(), "刷新过快或者网络连接异常");
//                    }
//                    if (mSwipeRefreshLayout != null) {
//                        mSwipeRefreshLayout.setRefreshing(false);
//                        mSwipeRefreshLayout.setEnabled(true);
//                    }
//                }
//            });
//        }
    }
}

