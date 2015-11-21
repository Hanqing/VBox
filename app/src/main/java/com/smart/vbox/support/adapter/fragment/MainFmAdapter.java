package com.smart.vbox.support.adapter.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.smart.vbox.support.helper.VbString;
import com.smart.vbox.ui.fragment.main.VideoFeedFragment;

/**
 * 主页Fragment适配器
 * @author lhq
 * created at 2015/10/24 10:13
 */
public class MainFmAdapter extends FragmentStatePagerAdapter {

    public MainFmAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new VideoFeedFragment();
            case 1:
                return new VideoFeedFragment();
//            case 2:
//                return new AcBangumiFragment();
//            case 3:
//                return AcEssayFragment.newInstance(AcString.ESSAY);
        }
        return null;
    }

    @Override
    public int getCount() {
        return VbString.MAIN_TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return VbString.MAIN_TITLES[position];
    }
}
