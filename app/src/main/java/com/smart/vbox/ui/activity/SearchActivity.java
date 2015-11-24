package com.smart.vbox.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.grpc.vbox.VBox;
import com.grpc.vbox.VBox_ServiceGrpc;
import com.smart.vbox.R;
import com.smart.vbox.support.GrpcManager;
import com.smart.vbox.support.adapter.recyclerview.SearchResultAdapter;
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
public class SearchActivity extends BaseActivity implements SearchResultAdapter.OnFeedItemClickListener{

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.search_view)
    SearchView mSearchView;

    @Bind(R.id.rv_video_search)
    RecyclerView rvSearchResult;

    private InputMethodManager inputMethodManager;
    private String currentSearchTip;
    private SearchResultAdapter searchAdapter;

    private int lastVisibleItem;
    private boolean isFeedEnd;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupFeed();
        setSearchView();
    }

    private void setupFeed() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvSearchResult.setLayoutManager(linearLayoutManager);

        searchAdapter = new SearchResultAdapter(this);
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

    }

    private void loadMore()
    {

    }

    private void setSearchView() {
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mSearchView.setIconifiedByDefault(true);
        mSearchView.setIconified(false);
        if (Build.VERSION.SDK_INT >= 14) {
            mSearchView.onActionViewExpanded();
        }
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            public boolean onQueryTextSubmit(String query) {
                hideSoftInput();
                search(query);
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.length() > 0) {
                    currentSearchTip = newText;
//                    showSearchTip(newText);
                }
                return true;
            }
        });

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void search(final String query) {
        Observable.create(new Observable.OnSubscribe<List<VBox.VObjectInfo>>() {
            @Override
            public void call(Subscriber<? super List<VBox.VObjectInfo>> subscriber) {
                try {
                    VBox_ServiceGrpc.VBox_ServiceBlockingStub vBoxStub = GrpcManager.getInstance().getStub();
                    VBox.ADSearchVideoReq req = VBox.ADSearchVideoReq.newBuilder().setTerminalMac(GlobalUtils.getLocalMacAddress(SearchActivity.this)).setSearchKey(query).setPageNum(5).build();
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
//                        feedAdapter.setVideoFeed(groupList);
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

    public void onCommentsClick(View v, int position) {
    }

    public void onProfileClick(View v) {
    }

}
