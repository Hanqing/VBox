package com.smart.vbox.ui.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.smart.vbox.R;
import com.smart.vbox.model.bean.UserEntity;

import cn.bingoogolapple.badgeview.BGABadgeImageView;

/**
 * @author lhq
 *         created at 2015/12/5 15:13
 */
public class UserInfoHeader implements View.OnClickListener {
    protected Activity mActivity;
    protected View mLayoutButton;
    protected BGABadgeImageView mImageViewAvatar;
    protected ImageView mImageViewGender;
    protected ImageView mImageViewEdit;
    protected TextView mTextViewScreenName;
    protected TextView mTextViewDescription;

    public UserInfoHeader(Activity context, View view) {
        this.mActivity = context;
        mImageViewAvatar = (BGABadgeImageView) view.findViewById(R.id.imageView_avatar);
        mTextViewScreenName = (TextView) view.findViewById(R.id.textView_nickName);
        mTextViewDescription = (TextView) view.findViewById(R.id.textView_description);
        mImageViewGender = (ImageView) view.findViewById(R.id.imageView_gender);
        mImageViewEdit = (ImageView) view.findViewById(R.id.icon_edit);
    }

    public void bindData(UserEntity entity) {
        if (entity != null) {
            entity.setVerified(true);
            mLayoutButton.setVisibility(View.GONE);
            mTextViewScreenName.setText(entity.getScreen_name());
            if (entity.getGender().contains("m"))
                mImageViewGender.setImageResource(R.drawable.ic_male_white_18dp);
            else {
                mImageViewGender.setImageResource(R.drawable.ic_female_white_18dp);
            }
        }

        mTextViewDescription.setOnClickListener(this);
        mImageViewEdit.setOnClickListener(this);
//        AppUtils.loadBigUserAvata(mActivity,entity, mImageViewAvatar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_description:
            case R.id.icon_edit:
                mActivity.startActivity(new Intent(mActivity, UserInfoEditActivity.class));
                break;
        }

    }
}
