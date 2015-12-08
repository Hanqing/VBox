package com.smart.vbox.ui.activity.user;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.smart.vbox.MyApplication;
import com.smart.vbox.R;
import com.smart.vbox.model.bean.UserEntity;
import com.smart.vbox.support.adapter.fragment.UserFmAdapter;
import com.smart.vbox.support.utils.ViewUtils;
import com.smart.vbox.ui.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * @author lhq
 *         created at 2015/12/5 15:05
 */
public class UserInfoActivity extends BaseActivity {
    private UserEntity mUserEntity;
    private UserInfoHeader mUserInfoHeader;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @Bind(R.id.viewpager)
    ViewPager mViewPager;

    @Bind(R.id.sliding_tabs)
    TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        mUserEntity = MyApplication.getsInstance().getUserEntity();

        mUserInfoHeader = new UserInfoHeader(this, findView(R.id.layout_header_view));
        mUserInfoHeader.bindData(mUserEntity);

        ViewUtils.setToolbarAsBack(this, mToolbar, "");

        mCollapsingToolbar.setTitle("主页");
        mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

        if (mTabLayout != null && mViewPager != null) {
            UserFmAdapter adapter = new UserFmAdapter(getSupportFragmentManager());
            //给ViewPager设置适配器
            mViewPager.setAdapter(adapter);
            mViewPager.setOffscreenPageLimit(4);
            mTabLayout.setupWithViewPager(mViewPager);
        }
    }

}
