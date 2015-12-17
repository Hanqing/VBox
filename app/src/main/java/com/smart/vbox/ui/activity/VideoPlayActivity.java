package com.smart.vbox.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smart.vbox.R;

import butterknife.ButterKnife;
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

    public final static String MEDIAS_ID_KEY = "media_id";
    private String mediaUrl = "http://v1.mukewang.com/d046e308-bf9e-4401-a4cf-e298873114de/H.mp4";

    @Bind(R.id.video_loading)
    View mLoadingView;

    @Bind(R.id.surface_view)
    VideoView mVideoView;

    @Bind(R.id.tv_noPlay)
    TextView mTv_NoPlay;

    @Bind(R.id.operation_bg)
    ImageView mOperationBg;

    @Bind(R.id.operation_percent)
    ImageView mOperationPercent;

    @Bind(R.id.operation_volume_brightness)
    View mVolumeBrightnessLayout;

    @Bind(R.id.fl_set_progress)
    View mFl_Progress;

    @Bind(R.id.tv_progress)
    TextView mTv_progress;

    @Bind(R.id.iv_progress_bg)
    ImageView mIv_Progress_bg;

    @Bind(R.id.rl_playView)
    RelativeLayout mRl_PlayView;

    private MediaController mMediaController;
    /**
     * 当前缩放模式
     */
    private int mLayout = VideoView.VIDEO_LAYOUT_STRETCH;
    private AudioManager mAudioManager;
    private GestureDetector mGestureDetector;
    private float mFast_forward;
    private boolean isFast_Forword;
    private boolean isUp_downScroll;

    /**
     * 是否自动恢复播放，用于自动暂停，恢复播放
     */
    private boolean needResume;

    /**
     * 最大声音
     */
    private int mMaxVolume;
    /**
     * 当前声音
     */
    private int mVolume = -1;
    /**
     * 当前亮度
     */
    private float mBrightness = -1f;

    public static void launch(Context context, String url) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(MEDIAS_ID_KEY, url);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LibsChecker.checkVitamioLibs(this))
            return;

        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);
        new PlayAsyncTask().execute("");
    }

    private void initVideoView() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnSeekCompleteListener(this);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        int mCurrentOrientation = getResources().getConfiguration().orientation;
        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            full(false, VideoPlayActivity.this);
            mRl_PlayView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));
            if (mVideoView != null)
                mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
            mMediaController = new MediaController(this);
            mVideoView.setMediaController(mMediaController);
        } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            full(true, VideoPlayActivity.this);
            mRl_PlayView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            if (mVideoView != null)
                mVideoView.setVideoLayout(mLayout, 0);
            mMediaController = new MediaController(this);
            mVideoView.setMediaController(mMediaController);
        }

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;

        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;
        if (isFast_Forword) {
            onSeekProgress(mFast_forward);
        }
        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 800);
    }

    class PlayAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            // PLAY
            initVideoView();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (mediaUrl.startsWith("http:"))
                mVideoView.setVideoURI(Uri.parse(mediaUrl));
            else
                mVideoView.setVideoPath(mediaUrl);
            // 设置显示名称
            mMediaController = new MediaController(VideoPlayActivity.this);
            mVideoView.setMediaController(mMediaController);
            mMediaController.setFileName("哈哈哈");

            int mCurrentOrientation = getResources().getConfiguration().orientation;
            if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                full(false, VideoPlayActivity.this);
                mRl_PlayView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));
                if (mVideoView != null) {
//					mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
                }
            } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                full(true, VideoPlayActivity.this);
                mRl_PlayView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                if (mVideoView != null)
                    mVideoView.setVideoLayout(mLayout, 0);
            }
            mVideoView.requestFocus();
            mGestureDetector = new GestureDetector(new MyGestureListener());
        }

    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mLayout == VideoView.VIDEO_LAYOUT_ZOOM)
                mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
            else
                mLayout++;
            if (mVideoView != null)
                mVideoView.setVideoLayout(mLayout, 0);
            return true;
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mMediaController.hide();
            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            int x = (int) e2.getRawX();
            Display disp = getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();

            if (Math.abs(x - mOldX) > 20 && !isUp_downScroll) { //执行快进快退
                isFast_Forword = true;
                mFast_forward = x - mOldX;
                fast_ForWord(mFast_forward);
            } else if (mOldX > windowWidth * 1.0 / 2 && Math.abs(mOldY - y) > 3 && !isFast_Forword)// 右边滑动
                onVolumeSlide((mOldY - y) / windowHeight);
            else if (mOldX < windowWidth / 2.0 && Math.abs(mOldY - y) > 3 && !isFast_Forword)// 左边滑动
                onBrightnessSlide((mOldY - y) / windowHeight);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    /**
     * 定时隐藏
     */
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            isFast_Forword = false;
            isUp_downScroll = false;
            mVolumeBrightnessLayout.setVisibility(View.GONE);
            mFl_Progress.setVisibility(View.GONE);
        }
    };

    private void onSeekProgress(float dis) {
        Log.e("position ==", mVideoView.getCurrentPosition() + 500 * (long) dis + "/" + mVideoView.getDuration());
        mVideoView.seekTo(mVideoView.getCurrentPosition() + 500 * (long) dis);
    }

    private void fast_ForWord(float dis) {
        long currentProgress;
        long duration = mVideoView.getDuration();
        if (mVideoView.getCurrentPosition() + 500 * (long) dis < 0)
            currentProgress = 0;
        else
            currentProgress = mVideoView.getCurrentPosition() + 500 * (long) dis;
        mTv_progress.setText(generateTime(currentProgress) + "/" + generateTime(duration));
        if (dis > 0)
            mIv_Progress_bg.setImageResource(R.drawable.btn_fast_forword);
        else
            mIv_Progress_bg.setImageResource(R.drawable.btn_back_forword);
        mFl_Progress.setVisibility(View.VISIBLE);
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        isUp_downScroll = true;
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

            // 显示
            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = findViewById(R.id.operation_full).getLayoutParams().width * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        isUp_downScroll = true;
        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            // 显示
            mOperationBg.setImageResource(R.drawable.video_brightness_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);
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
                // �?��缓存，暂停播�?
                if (isPlaying()) {
                    stopPlayer();
                    needResume = true;
                }
                mLoadingView.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                // 缓存完成，继续播�?
                if (needResume) {
                    startPlayer();
                }
                mLoadingView.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                // 显示 下载速度
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
        mTv_NoPlay.setVisibility(View.VISIBLE);
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
        mTv_NoPlay.setVisibility(View.GONE);
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

    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
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
