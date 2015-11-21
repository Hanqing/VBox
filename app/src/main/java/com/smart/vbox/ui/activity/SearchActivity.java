package com.smart.vbox.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.smart.vbox.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hanqing on 2015/11/21.
 */
public class SearchActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.search_view)
    SearchView mSearchView;

    private InputMethodManager inputMethodManager;
    private String currentSearchTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setSearchView();


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
}
