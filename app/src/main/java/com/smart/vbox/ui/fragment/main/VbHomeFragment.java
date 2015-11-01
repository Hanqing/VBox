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

import com.smart.vbox.R;
import com.smart.vbox.support.adapter.recyclerview.VbRecommendRvAdapter;
import com.smart.vbox.support.utils.ViewUtils;
import com.smart.vbox.ui.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 
 * @author lhq
 * created at 2015/10/24 10:32
 */
public class VbHomeFragment extends BaseFragment {
    private boolean mIsPrepared;
    private VbRecommendRvAdapter mAdapter;
//    private VbApi.getVbRecommend VbRecommend;

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
//                getHttpResult(VbString.BANNER);
//                getHttpResult(VbString.HOT);
//                getHttpResult(VbString.ANIMATION);
//                getHttpResult(VbString.FUN);
//                getHttpResult(VbString.MUSIC);
//                getHttpResult(VbString.GAME);
//                getHttpResult(VbString.SCIENCE);
//                getHttpResult(VbString.SPORT);
//                getHttpResult(VbString.TV);
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
//                        getHttpResult(VbString.HOT);
                        mSwipeRefreshLayout.setRefreshing(true);
                        mSwipeRefreshLayout.setEnabled(false);
                    }
                });
            }
        }

    }

    private void getHttpResult(String httpGetType) {
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

