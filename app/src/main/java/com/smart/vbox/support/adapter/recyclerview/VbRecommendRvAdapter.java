package com.smart.vbox.support.adapter.recyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.grpc.xbox.xbox;
import com.smart.vbox.R;
import com.smart.vbox.support.callback.GetVbHomePageHttpResult;
import com.smart.vbox.support.utils.GlobalUtils;


import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * @author lhq
 * created at 2015/10/31 17:00
 */
public class VbRecommendRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements GetVbHomePageHttpResult {
    private static final int TYPE_NAVIGATION_TITLE = 0;
    private static final int TYPE_CARD_VIEW = 1;

    private ArrayList<xbox.vObjectGroup> mHomePageGroupList = new ArrayList<xbox.vObjectGroup>();

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public void setSwipeRefreshLayout(SwipeRefreshLayout mSwipeRefreshLayout) {
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
    }

    public ArrayList<xbox.vObjectGroup> getHomePageGroup() {
        return mHomePageGroupList;
    }

    @Override
    public void onHomePageResult(ArrayList<xbox.vObjectGroup> result) {
        this.mHomePageGroupList = result;
        notifyDataSetChanged();
    }

    public interface OnClickListener {
        void onClick(View view, String partitionType, String contentId);
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }


    //分区标题
    public class NavigationTitleVH extends RecyclerView.ViewHolder {
        @Bind(R.id.ac_fragment_re_partition_title_tv)
        TextView tvPartitionTitle;

        @Bind(R.id.ac_fragment_re_partition_title_btn)
        Button btnPartitionMore;

        public NavigationTitleVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //视图卡片
    public class HotVH extends RecyclerView.ViewHolder {
        @Bind(R.id.ac_card_view_tv_1)
        TextView tvTitleHot;

        @Bind(R.id.ac_card_view_tv_2)
        TextView tvSubTitleHot;

        @Bind(R.id.ac_card_view_img)
        SimpleDraweeView imgCoverHot;

        @Bind(R.id.ac_fragment_re_card_view)
        CardView cardViewHot;

        public HotVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (getTitleType(position)) {
            return TYPE_NAVIGATION_TITLE;
        } else {
            return TYPE_CARD_VIEW;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View title
                = LayoutInflater.from(parent.getContext()).inflate(R.layout.ac_rv_title_with_button, parent, false);
        View hot
                = LayoutInflater.from(parent.getContext()).inflate(R.layout.ac_rv_cardview_vertical_with_subtitle, parent, false);

        if (viewType == TYPE_NAVIGATION_TITLE) {
            return new NavigationTitleVH(title);
        } else if (viewType == TYPE_CARD_VIEW) {
            return new HotVH(hot);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NavigationTitleVH) {
            ((NavigationTitleVH) holder).tvPartitionTitle.setText("");
            if (mOnClickListener != null) {
                ((NavigationTitleVH) holder).btnPartitionMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnClickListener.onClick(v, null, null);
                    }
                });
            }
        } else if (holder instanceof HotVH) {
//            if (position == 2 | position == 3 | position == 4 | position == 5 && mAcReHot != null) {
//                final AcReHot.DataEntity.PageEntity.ListEntity entity
//                        = mAcReHot.getData().getPage().getList().get(position - 2);
//
//                ((HotVH) holder).imgCoverHot.setImageURI(Uri.parse(entity.getCover()));
//                ((HotVH) holder).tvTitleHot.setText(entity.getTitle());
//                ((HotVH) holder).tvSubTitleHot.setText(entity.getSubtitle());
//
//                if (mOnClickListener != null) {
//                    ((HotVH) holder).cardViewHot.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mOnClickListener.onClick(v, null, entity.getSpecialId());
//                        }
//                    });
//                }
//            } else {
//                ((HotVH) holder).imgCoverHot.setImageURI(null);
//                ((HotVH) holder).tvTitleHot.setText("");
//                ((HotVH) holder).tvSubTitleHot.setText("");
//
//                if (mOnClickListener != null) {
//                    ((HotVH) holder).cardViewHot.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                        }
//                    });
//                }
//            }
        }
//        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return mHomePageGroupList.size() * 5;
    }

    //根据position判断是否显示分区标题
    public boolean getTitleType(int position) {
        if (position % 5 == 0) {
            return true;
        }
        return false;
    }


    public void setCardViewOtherOnClickListener(CardView cardViewOther, final String partitionType, final String contentId) {
        if (mOnClickListener != null) {
            if (partitionType == null && contentId == null) {
                cardViewOther.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
            cardViewOther.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onClick(v, partitionType, contentId);
                }
            });
        }
    }

    //处理cardView中间的margin
    public static class MyDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
            int marginRight = GlobalUtils.dip2pix(parent.getContext(), 7);
            if (position == 2 | position == 4 | position == 7 | position == 10 | position == 13
                    | position == 16 | position == 19 | position == 22 | position == 25) {
                outRect.set(0, 0, marginRight, 0);
            }
        }
    }
}
