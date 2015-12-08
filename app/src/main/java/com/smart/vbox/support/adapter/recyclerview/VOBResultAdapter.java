package com.smart.vbox.support.adapter.recyclerview;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.grpc.vbox.VBox;
import com.smart.vbox.R;
import com.smart.vbox.support.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hanqing on 2015/11/24.
 */
public class VOBResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final int ANIMATED_ITEMS_COUNT = 2;

    private Context context;
    private int lastAnimatedPosition = -1;
    private boolean animateItems = false;

    private List<VBox.VObjectInfo> mVideoList = new ArrayList<VBox.VObjectInfo>();
    private OnFeedItemClickListener onFeedItemClickListener;


    public VOBResultAdapter(Context context) {
        this.context = context;
    }

    public void setVideoFeed(List<VBox.VObjectInfo> videoList) {
        mVideoList = videoList;
        notifyDataSetChanged();
    }

    public List<VBox.VObjectInfo> getVideoFeed() {
        return mVideoList;
    }

    public void addVideoFeed(List<VBox.VObjectInfo> videoList) {
        if (mVideoList != null && videoList != null) {
            List<VBox.VObjectInfo> newList = new ArrayList<VBox.VObjectInfo>();
            newList.addAll(mVideoList);
            newList.addAll(videoList);

            mVideoList = newList;
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        final CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
        cellFeedViewHolder.ivPoster.setOnClickListener(this);
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
        bindDefaultSearchItem(position, holder);
    }

    private void bindDefaultSearchItem(int position, CellFeedViewHolder holder) {
        VBox.VObjectInfo video = mVideoList.get(position);
        Log.i("xixi", "Image url : " + video.getVideoImagePath());
        holder.ivPoster.setImageURI(Uri.parse(video.getVideoImagePath()));
        holder.ivVideoTitle.setText(video.getVideoTitle());
        holder.ivVideoYear.setText(video.getVDate());
        holder.ivVideoPerformer.setText(video.getVideoDirector());

        holder.ivPoster.setTag(video);
    }


    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == R.id.ivPoster) {
            Log.i("xixi", "onFeedItemClickListener : " + (onFeedItemClickListener != null));
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onCommentsClick(view, (VBox.VObjectInfo) view.getTag());
            }
        } else if (viewId == R.id.btnLike) {

        } else if (viewId == R.id.ivFeedCenter) {

        }
    }


    public void updateItems(boolean animated) {
        animateItems = animated;
        notifyDataSetChanged();
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivPoster)
        SimpleDraweeView ivPoster;
        @Bind(R.id.ivVideoTitle)
        TextView ivVideoTitle;
        @Bind(R.id.ivVideoYear)
        TextView ivVideoYear;
        @Bind(R.id.ivVideoPerformer)
        TextView ivVideoPerformer;


        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    public interface OnFeedItemClickListener {
        public void onCommentsClick(View v, VBox.VObjectInfo vObjectInfo);

        public void onProfileClick(View v);
    }
}
