package com.smart.vbox.support.adapter.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.smart.vbox.support.helper.VbString;
import com.smart.vbox.ui.fragment.main.FavorFragment;
import com.smart.vbox.ui.fragment.main.MovieFeedFragment;
import com.smart.vbox.ui.fragment.main.ShortVideoFeedFragment;

/**
 * 主页Fragment适配器
 * @author lhq
 * created at 2015/10/24 10:13
 */
public class UserFmAdapter extends FragmentStatePagerAdapter {

    public UserFmAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FavorFragment();
            case 1:
                return new FavorFragment();
            case 2:
                return new FavorFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return VbString.USER_INFO_TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return VbString.USER_INFO_TITLES[position];
    }
}
