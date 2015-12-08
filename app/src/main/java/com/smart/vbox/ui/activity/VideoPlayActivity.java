package com.smart.vbox.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;

import com.smart.vbox.R;
import com.smart.vbox.support.utils.DensityUtil;
import com.smart.vbox.support.utils.DeviceUtils;
import com.smart.vbox.support.utils.ViewUtils;
import com.smart.vbox.support.widget.VideoControllerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.vov.vitamio.LibsChecker;

public class VideoPlayActivity extends BaseActivity {
    public final static String MEDIAS_ID_KEY = "media_id";

    @Bind(R.id.tool_bar)
    Toolbar mToolbar;

    @Bind(R.id.appbar)
    AppBarLayout mAppBar;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingoolbar;

    @Bind(R.id.videoControllerView)
    VideoControllerView mVideoController;

    private String mediaUrl;

    public static void launch(Context context, String url) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(MEDIAS_ID_KEY, url);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);


        int width = DeviceUtils.getScreenWidth(this) + DensityUtil.dip2px(this, 110) + getActionBarSize();
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, width);
        mAppBar.setLayoutParams(params);

        ViewUtils.setToolbarAsBack(this, mToolbar, "");

        mCollapsingoolbar.setTitle(" ");
        mCollapsingoolbar.setExpandedTitleColor(Color.TRANSPARENT);

        mediaUrl = getIntent().getStringExtra(MEDIAS_ID_KEY);

        Log.i("xixi", "Media url : " + mediaUrl);
        if (mVideoController != null) {
            mVideoController.setVideoPath(mediaUrl);
            mVideoController.start();
        }
    }


    public int getActionBarSize() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return 0;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoController != null)
            mVideoController.release();
    }
}
