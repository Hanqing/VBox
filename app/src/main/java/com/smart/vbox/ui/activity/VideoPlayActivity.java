package com.smart.vbox.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.grpc.vbox.VBox;
import com.smart.vbox.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import cn.bingoogolapple.badgeview.BGABadgeTextView;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;

import butterknife.Bind;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoPlayActivity extends BaseActivity implements View.OnClickListener, OnCompletionListener, OnInfoListener,
        OnPreparedListener, OnErrorListener, OnBufferingUpdateListener, OnSeekCompleteListener {

    public final static String TAG = "VideoPlayActivity";
    public final static String VIDEO_TYPE = "video_type";
    public final static String VOBJECTINFO_KEY = "vobjectinfo_key";
    public final static String EPISODENUM_KEY = "episodenum_key";

    private String mediaUrl = "http://v1.mukewang.com/d046e308-bf9e-4401-a4cf-e298873114de/H.mp4";

    @Bind(R.id.video_loading)
    View mLoadingView;

    @Bind(R.id.surface_view)
    VideoView mVideoView;

    @Bind(R.id.tv_no_play)
    TextView mNoPlayView;

    @Bind(R.id.episode_flowlayout)
    TagFlowLayout mEpisodeFlow;

    private MediaController mMediaController;
    /**
     * 当前缩放模式
     */
    private int mLayout = VideoView.VIDEO_LAYOUT_STRETCH;

    /**
     * 是否自动恢复播放，用于自动暂停，恢复播放
     */
    private boolean needResume;

    private VBox.VObjectInfo vObjectInfo;

    private int episodeNum;

    private List<VBox.VideoInfo> videoInfoList;

    /**
     * 跳转播放页面
     *
     * @param context     上下文
     * @param vObjectInfo 视频对象详细信息
     * @param episodeNum  默认播放电视剧第几集
     */
    public static void launch(Context context, VBox.VObjectInfo vObjectInfo, int episodeNum) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(VOBJECTINFO_KEY, vObjectInfo);
        bundle.putInt(EPISODENUM_KEY, episodeNum);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LibsChecker.checkVitamioLibs(this))
            return;

        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);

        initBundle();
        initVideoView();
        initEpisodeFlow();
    }

    private void initBundle() {
        Intent intent = getIntent();

        if (intent != null) {
            vObjectInfo = (VBox.VObjectInfo) intent.getSerializableExtra(VOBJECTINFO_KEY);
            episodeNum = intent.getIntExtra(EPISODENUM_KEY, 1);

            Log.i(TAG, "vObjectInfo : " + vObjectInfo.getVEpisodeNums() + ", episodeNum : " + episodeNum);
        }

        if (vObjectInfo != null) {
            videoInfoList = vObjectInfo.getVideoPlayGroupList();
        }
    }

    private void initVideoView() {
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnSeekCompleteListener(this);

        mMediaController = new MediaController(VideoPlayActivity.this);
        mVideoView.setMediaController(mMediaController);
        mVideoView.requestFocus();

        if (mediaUrl.startsWith("http:"))
            mVideoView.setVideoURI(Uri.parse(mediaUrl));
        else
            mVideoView.setVideoPath(mediaUrl);
    }

    /**
     * 初始化集数显示
     */
    private void initEpisodeFlow() {
        final LayoutInflater mInflater = LayoutInflater.from(this);

        final String[] mVals;
        if (vObjectInfo == null || vObjectInfo.getVEpisodeNums() < 0) {
            return;
        } else {
            int episodeNums = vObjectInfo.getVEpisodeNums();
            mVals = new String[episodeNums];
            for (int i = 0; i < episodeNums; i++) {
                mVals[i] = String.valueOf(i + 1);
            }
        }

        mEpisodeFlow.setAdapter(new TagAdapter<String>(mVals) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                BGABadgeTextView tv = (BGABadgeTextView) mInflater.inflate(R.layout.episode_grid_item,
                        mEpisodeFlow, false);
                tv.setText(s);
                return tv;
            }
        });

        mEpisodeFlow.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Log.i("xixi", "On Tag Click");
                Toast.makeText(VideoPlayActivity.this, mVals[position], Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mEpisodeFlow.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                Log.i("xixi", "onSelected");
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        int mCurrentOrientation = getResources().getConfiguration().orientation;
        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            full(false, VideoPlayActivity.this);
            if (mVideoView != null)
                mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
            mMediaController = new MediaController(this);
            mVideoView.setMediaController(mMediaController);
        } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            full(true, VideoPlayActivity.this);
            if (mVideoView != null)
                mVideoView.setVideoLayout(mLayout, 0);
            mMediaController = new MediaController(this);
            mVideoView.setMediaController(mMediaController);
        }

        super.onConfigurationChanged(newConfig);
    }

    private void stopPlayer() {
        if (mVideoView != null)
            mVideoView.pause();
    }

    private void startPlayer() {
        if (mVideoView != null)
            mVideoView.start();
    }

    private boolean isPlaying() {
        return mVideoView != null && mVideoView.isPlaying();
    }

    @Override
    public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
        switch (arg1) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (isPlaying()) {
                    stopPlayer();
                    needResume = true;
                }
                mLoadingView.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (needResume) {
                    startPlayer();
                }
                mLoadingView.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                // 显示下载速度
                break;
        }
        return true;
    }

    /**
     * 播放完成
     */
    @Override
    public void onCompletion(MediaPlayer arg0) {
    }

    /**
     * //在视频预处理完成后调用。在视频预处理完成后被调用。此时视频的宽度、高度、宽高比信息已经获取到，此时可调用seekTo让视频从指定位置开始播放。
     */
    @Override
    public void onPrepared(MediaPlayer arg0) {
    }

    /**
     * 在异步操作调用过程中发生错误时调用。例如视频打开失败。
     */
    @Override
    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
        mLoadingView.setVisibility(View.GONE);
        mNoPlayView.setVisibility(View.VISIBLE);
        return false;
    }

    /**
     * 在网络视频流缓冲变化时调用。
     *
     * @param arg0
     * @param arg1
     */
    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
        mNoPlayView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    /**
     * 在seek操作完成后调用。
     */
    @Override
    public void onSeekComplete(MediaPlayer arg0) {
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

    }

    public static void full(boolean enable, Activity activity) {
        if (enable) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(lp);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attr);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
}
