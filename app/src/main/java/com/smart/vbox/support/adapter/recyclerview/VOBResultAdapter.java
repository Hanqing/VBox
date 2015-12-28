package com.smart.vbox.support.adapter.recyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.grpc.vbox.VBox;
import com.grpc.vbox.VBox_ServiceGrpc;
import com.smart.vbox.R;
import com.smart.vbox.support.GrpcManager;
import com.smart.vbox.support.utils.ViewUtils;
import com.smart.vbox.support.widget.BGABadgeButton;
import com.smart.vbox.ui.activity.VideoPlayActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Hanqing on 2015/11/24.
 */
public class VOBResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "VOBResultAdapter";
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
        cellFeedViewHolder.episode1.setOnClickListener(this);
        cellFeedViewHolder.episode2.setOnClickListener(this);
        cellFeedViewHolder.episode3.setOnClickListener(this);
        cellFeedViewHolder.episode4.setOnClickListener(this);
        cellFeedViewHolder.episode5.setOnClickListener(this);
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
        holder.ivPoster.setImageURI(Uri.parse(video.getVideoImagePath()));
        holder.ivVideoTitle.setText(video.getVideoTitle());
        holder.ivVideoYear.setText(video.getVDate());
        holder.ivVideoPerformer.setText(video.getVideoDirector());
        holder.ivPoster.setTag(video);
        holder.episode1.setTag(video);

        int episodeNums = video.getVEpisodeNums();
        int epHelperNums = Math.min(5, episodeNums);

        Log.i(TAG, "视频" + video.getVideoTitle() + "集数 : " + episodeNums + ", 详情 : " + video.toString());

        List<VBox.VideoInfo> videoList = video.getVideoPlayGroupList();
        if (videoList == null) {
            Log.i(TAG, "Video List is NULL");
        } else {
            Log.i(TAG, "Video List num : " + videoList.size());
        }
        if (episodeNums <= 1) {
            holder.episodeContainer.setVisibility(View.GONE);
            return;
        }

        switch (epHelperNums) {
            case 5:
                holder.episode5.setVisibility(View.VISIBLE);
            case 4:
                holder.episode4.setVisibility(View.VISIBLE);
            case 3:
                holder.episode3.setVisibility(View.VISIBLE);
            case 2:
                holder.episode2.setVisibility(View.VISIBLE);
            case 1:
                holder.episode1.setVisibility(View.VISIBLE);
        }

        if (episodeNums > 5) {
            holder.episode5.setText("更多");
        }

        for (VBox.VideoInfo info : video.getVideoPlayGroupList()) {
            Log.i(TAG, "视频具体某集详情 : " + info.toString());
            switch (info.getPlayEpisodeNum()) {
//                case 1:
//                    showDownloadState(holder.episode1, info.getVideoStatus());
//                    break;
//                case 2:
//                    showDownloadState(holder.episode2, info.getVideoStatus());
//                    break;
//                case 3:
//                    showDownloadState(holder.episode3, info.getVideoStatus());
//                    break;
//                case 4:
//                    showDownloadState(holder.episode4, info.getVideoStatus());
//                    break;
//                case 5:
//                    showDownloadState(holder.episode5, info.getVideoStatus());
//                    break;
            }
        }
    }

    private void showDownloadState(BGABadgeButton btn, VBox.TaskStatusDef status) {
        Bitmap statusProgress = BitmapFactory.decodeResource(context.getResources(), R.drawable.badge_download_inprogress);
        Bitmap statusFailed = BitmapFactory.decodeResource(context.getResources(), R.drawable.badge_download_failed);
        Bitmap statusDone = BitmapFactory.decodeResource(context.getResources(), R.drawable.badge_download_done);
        switch (status) {
            case TaskStatusDownloading:
                btn.showDrawableBadge(statusProgress);
                break;
            case TaskStatusDownloadFailed:
                btn.showDrawableBadge(statusFailed);
                break;
            case TaskStatusDownloadCompleted:
                btn.showDrawableBadge(statusDone);
                break;
        }
    }

    private void toDownload(final int videoId) {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    VBox_ServiceGrpc.VBox_ServiceBlockingStub vBoxStub = GrpcManager.getInstance().getStub();
                    VBox.ADAddTaskReq req = VBox.ADAddTaskReq.newBuilder().setVideoID(videoId).build();
                    VBox.ADAddTaskRsp res = vBoxStub.addTask(req);

                    subscriber.onNext(res.getRetCode());

                } catch (SecurityException | UncheckedExecutionException e) {
                    e.printStackTrace();
                    subscriber.onNext(null);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer retCode) {
                        Log.i(TAG, "Add download task retcode : " + retCode);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        switch (viewId) {
            case R.id.ivPoster:
                Log.i("xixi", "onFeedItemClickListener : " + (onFeedItemClickListener != null));
                if (onFeedItemClickListener != null) {
                    onFeedItemClickListener.onCommentsClick(view, (VBox.VObjectInfo) view.getTag());
                }
                break;
            case R.id.episode_1:
                VBox.VObjectInfo info = (VBox.VObjectInfo) view.getTag();
                if (info != null) {
                    VideoPlayActivity.launch(context, info, 1);
                }
                break;
            case R.id.episode_2:
                break;
            case R.id.episode_3:
                break;
            case R.id.episode_4:
                break;
            case R.id.episode_5:
                break;
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

        @Bind(R.id.episode_container)
        LinearLayout episodeContainer;
        @Bind(R.id.episode_1)
        BGABadgeButton episode1;
        @Bind(R.id.episode_2)
        BGABadgeButton episode2;
        @Bind(R.id.episode_3)
        BGABadgeButton episode3;
        @Bind(R.id.episode_4)
        BGABadgeButton episode4;
        @Bind(R.id.episode_5)
        BGABadgeButton episode5;

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
