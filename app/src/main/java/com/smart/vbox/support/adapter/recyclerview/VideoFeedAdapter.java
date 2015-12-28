package com.smart.vbox.support.adapter.recyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.grpc.vbox.VBox;
import com.smart.vbox.R;
import com.smart.vbox.support.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


/**
 * 小片列表Adapter
 *
 * @author lhq
 *         created at 2015/11/20 21:08
 */
public class VideoFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final int ANIMATED_ITEMS_COUNT = 2;

    private Context context;
    private int lastAnimatedPosition = -1;
    private boolean animateItems = false;

    private List<VBox.VAbstractVideoObject> mVideoList = new ArrayList<>();
    private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap<>();
    private OnFeedItemClickListener onFeedItemClickListener;


    public VideoFeedAdapter(Context context) {
        this.context = context;
    }

    public void setVideoFeed(List<VBox.VAbstractVideoObject> videoList) {
        mVideoList = videoList;
        notifyDataSetChanged();
    }

    public List<VBox.VAbstractVideoObject> getVideoFeed() {
        return mVideoList;
    }

    public void addVideoFeed(List<VBox.VAbstractVideoObject> videoList) {
        if (mVideoList != null && videoList != null) {
            List<VBox.VAbstractVideoObject> newList = new ArrayList<VBox.VAbstractVideoObject>();
            newList.addAll(mVideoList);
            newList.addAll(videoList);

            mVideoList = newList;
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_video_feed, parent, false);
        final CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
        cellFeedViewHolder.btnComments.setOnClickListener(this);
        cellFeedViewHolder.ivFeedCenter.setOnClickListener(this);
        cellFeedViewHolder.btnLike.setOnClickListener(this);

        return cellFeedViewHolder;
    }

    private void runEnterAnimation(View view, int position) {
        if (!animateItems || position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(ViewUtils.getScreenHeight(context));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        runEnterAnimation(viewHolder.itemView, position);
        final CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;
        bindDefaultFeedItem(position, holder);
    }

    private void bindDefaultFeedItem(int position, CellFeedViewHolder holder) {
        VBox.VAbstractVideoObject video = mVideoList.get(position);
        holder.ivFeedCenter.setImageURI(Uri.parse(video.getVideoPosterUrl()));
        holder.ivFeedTitle.setText(video.getVideoTitle());
        updateLikesCounter(holder, video.getVideoPraiseNum(), false);
        //TODO getIsFavour()值定义 host用户是否已经顶/踩过  1-顶 2-踩 0-都没有
        updateHeartButton(holder, video.getIsFavour() == 1, false);

        holder.btnComments.setTag(position);
        holder.ivFeedCenter.setTag(holder);
        holder.btnLike.setTag(holder);
        holder.mVideoView.setTag(position);

        setVideoView(holder);

        if (likeAnimations.containsKey(holder)) {
            likeAnimations.get(holder).cancel();
        }
        resetLikeAnimationState(holder);
    }

    private void setVideoView(CellFeedViewHolder holder) {

        final MediaController mediaController = new MediaController(context, true, holder.fVideoRoot);
        holder.mVideoView.setMediaController(mediaController);
        mediaController.setVisibility(View.GONE);
        //在有警告或错误信息时调用。例如：开始缓冲、缓冲结束、下载速度变化。
        holder.mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        //在网络视频流缓冲变化时调用。
        holder.mVideoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {

            }
        });
        holder.mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    private void updateLikesCounter(CellFeedViewHolder holder, int likeCount, boolean animated) {
        String likesCountText = context.getResources().getQuantityString(
                R.plurals.likes_count, likeCount, likeCount
        );

        if (animated) {
            holder.tsLikesCounter.setText(likesCountText);
        } else {
            holder.tsLikesCounter.setCurrentText(likesCountText);
        }
    }

    private void updateHeartButton(final CellFeedViewHolder holder, boolean liked, boolean animated) {
        if (animated) {
            if (!likeAnimations.containsKey(holder)) {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.btnLike, "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.btnLike, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.btnLike, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.btnLike.setImageResource(R.drawable.ic_heart_red);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetLikeAnimationState(holder);
                    }
                });

                animatorSet.start();
            }
        } else {
            if (liked) {
                holder.btnLike.setImageResource(R.drawable.ic_heart_red);
            } else {
                holder.btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
            }
        }
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == R.id.btnComments) {
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onCommentsClick(view, (Integer) view.getTag());
            }
        } else if (viewId == R.id.btnLike) {
            CellFeedViewHolder holder = (CellFeedViewHolder) view.getTag();
            int position = (int) holder.mVideoView.getTag();
            VBox.VAbstractVideoObject video = mVideoList.get(position);
            if (video.getIsFavour() == 0) {
                //TODO 进行服务器请求
                updateLikesCounter(holder, video.getVideoPraiseNum(), true);
                updateHeartButton(holder, true, true);
            }
        } else if (viewId == R.id.ivFeedCenter) {
            CellFeedViewHolder holder = (CellFeedViewHolder) view.getTag();
            holder.ivFeedCenter.setVisibility(View.GONE);
            int position = (int) holder.mVideoView.getTag();
            VBox.VAbstractVideoObject video = mVideoList.get(position);
            Uri uri = Uri.parse(video.getVideoPlayUrl());
//            Uri uri = Uri.parse("http://v1.mukewang.com/63999667-ff61-4606-97eb-8a3faec16c0f/H.mp4");
//            Uri uri = Uri.parse("http://222.190.122.89:8001/看江北367.wmv");
//            Uri uri = Uri.parse("http://222.190.122.89:8001/20151117pt.flv");
            playVideo(holder.mVideoView, uri);
        }
    }

    /**
     * 播放视频
     *
     * @author lhq
     * created at 2015/11/20 21:19
     */
    private void playVideo(VideoView videoView, Uri uri) {
        videoView.setVisibility(View.VISIBLE);
        videoView.requestFocus();
        videoView.setKeepScreenOn(true);
        videoView.setVideoURI(uri);
    }

    private void resetLikeAnimationState(CellFeedViewHolder holder) {
        likeAnimations.remove(holder);
    }

    public void updateItems(boolean animated) {
        animateItems = animated;
        notifyDataSetChanged();
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivFeedCenter)
        SimpleDraweeView ivFeedCenter;
        @Bind(R.id.buffer)
        VideoView mVideoView;
        @Bind(R.id.ivFeedTitle)
        TextView ivFeedTitle;
        @Bind(R.id.btnComments)
        ImageButton btnComments;
        @Bind(R.id.btnLike)
        ImageButton btnLike;
        @Bind(R.id.tsLikesCounter)
        TextSwitcher tsLikesCounter;
        @Bind(R.id.fVideoRoot)
        FrameLayout fVideoRoot;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    public interface OnFeedItemClickListener {
        public void onCommentsClick(View v, int position);

        public void onProfileClick(View v);
    }
}
