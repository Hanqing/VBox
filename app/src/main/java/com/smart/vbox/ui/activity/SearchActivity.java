package com.smart.vbox.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.grpc.vbox.VBox;
import com.grpc.vbox.VBox_ServiceGrpc;
import com.smart.vbox.R;
import com.smart.vbox.support.GrpcManager;
import com.smart.vbox.support.adapter.recyclerview.VOBResultAdapter;
import com.smart.vbox.support.utils.GlobalUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Hanqing on 2015/11/21.
 */
public class SearchActivity extends BaseActivity implements VOBResultAdapter.OnFeedItemClickListener {

    @Bind(R.id.search_view)
    SearchView mSearchView;

    @Bind(R.id.rv_video_search)
    RecyclerView rvSearchResult;

    @Bind(R.id.searchback)
    ImageButton searchBack;

    private InputMethodManager inputMethodManager;
    private String currentQueryText;
    private VOBResultAdapter searchAdapter;

    private int lastVisibleItem;
    private boolean isFeedEnd;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSearchView();
        setupFeed();
    }

    private void setupFeed() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvSearchResult.setLayoutManager(linearLayoutManager);

        searchAdapter = new VOBResultAdapter(this);
        searchAdapter.setOnFeedItemClickListener(this);
        rvSearchResult.setAdapter(searchAdapter);

        //RecyclerView滑动监听
        rvSearchResult.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLoading || isFeedEnd) {
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == searchAdapter.getItemCount()) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

        searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void loadMore() {
        Observable.create(new Observable.OnSubscribe<List<VBox.VObjectInfo>>() {
            @Override
            public void call(Subscriber<? super List<VBox.VObjectInfo>> subscriber) {
                try {
                    isLoading = true;
                    VBox_ServiceGrpc.VBox_ServiceBlockingStub vBoxStub = GrpcManager.getInstance().getStub();
                    VBox.ADSearchVideoReq req = VBox.ADSearchVideoReq.newBuilder().setTerminalMac(GlobalUtils.getLocalMacAddress(SearchActivity.this)).setSearchKey(currentQueryText).setPageNum(5).build();
                    VBox.ADSearchVideoRsp res = vBoxStub.searchVideo(req);

                    subscriber.onNext(res.getSearchObjsList());

                    Log.i("xixi", "" + res.getSearchObjsList().size());

                } catch (SecurityException | UncheckedExecutionException e) {
                    e.printStackTrace();
                    subscriber.onNext(null);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<VBox.VObjectInfo>>() {
                    @Override
                    public void call(List<VBox.VObjectInfo> groupList) {
                        // 进行显示
                        if (groupList == null || groupList.isEmpty()) {
                            return;
                        }
                        searchAdapter.setVideoFeed(groupList);
                        /* shutdownChannel(); */
                    }
                });
    }

    private void setSearchView() {
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mSearchView.setIconifiedByDefault(false);
        if (Build.VERSION.SDK_INT >= 14) {
            mSearchView.onActionViewExpanded();
        }
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.i("xixi", "On Close");
                return true;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            public boolean onQueryTextSubmit(String query) {
                hideSoftInput();
                currentQueryText = query;
                search();
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.length() > 0) {
//                    showSearchTip(newText);
                }
                return true;
            }
        });

        // text color
        AutoCompleteTextView searchText = (AutoCompleteTextView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(ContextCompat.getColor(this, R.color.white));
        searchText.setHintTextColor(ContextCompat.getColor(this, R.color.search_hint));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void search() {
        Observable.create(new Observable.OnSubscribe<List<VBox.VObjectInfo>>() {
            @Override
            public void call(Subscriber<? super List<VBox.VObjectInfo>> subscriber) {
                try {
                    VBox_ServiceGrpc.VBox_ServiceBlockingStub vBoxStub = GrpcManager.getInstance().getStub();
                    VBox.ADSearchVideoReq req = VBox.ADSearchVideoReq.newBuilder().setTerminalMac(GlobalUtils.getLocalMacAddress(SearchActivity.this)).setSearchKey(currentQueryText).setVideoStartSeqNum(1).setPageNum(5).build();
                    VBox.ADSearchVideoRsp res = vBoxStub.searchVideo(req);

                    Log.i("xixi", "res : " + res.getSearchObjsList().size());
                    subscriber.onNext(res.getSearchObjsList());

                } catch (SecurityException | UncheckedExecutionException e) {
                    e.printStackTrace();
                    subscriber.onNext(null);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<VBox.VObjectInfo>>() {
                    @Override
                    public void call(List<VBox.VObjectInfo> groupList) {
                        // 进行显示
                        if (groupList == null || groupList.isEmpty()) {
                            return;
                        }
                        searchAdapter.setVideoFeed(groupList);
                        /* shutdownChannel(); */
                    }
                });
    }

    /**
     * hide soft input
     */
    private void hideSoftInput() {
        if (inputMethodManager != null) {
            View v = this.getCurrentFocus();
            if (v == null) {
                return;
            }

            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            mSearchView.clearFocus();
        }
    }

    public void onCommentsClick(View v, VBox.VObjectInfo vObjectInfo) {
        Log.i("xixi", "" + vObjectInfo.getVideoPlayGroupCount());
        List<VBox.VideoInfo> videos = vObjectInfo.getVideoPlayGroupList();
        for (VBox.VideoInfo info : videos)
        {
            Log.i("xixi", "INFO : " + info.getPlayEpisodePlayUrl());
        }
//        VideoPlayActivity.launch(this, vObjectInfo.getVideoPlayGroup(0).getPlayEpisodePlayUrl());
        VideoPlayActivity.launch(this, "");
    }

    public void onProfileClick(View v) {
    }

}
