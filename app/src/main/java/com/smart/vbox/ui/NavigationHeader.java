package com.smart.vbox.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.smart.vbox.R;
import com.smart.vbox.ui.activity.user.UserInfoActivity;


/**
 * @author lhq
 *         created at 2015/12/5 14:28
 */
public class NavigationHeader implements View.OnClickListener {
    private ImageView mImageViewAvatar;
    private TextView mTextViewNickName;
    private Activity mActivity;

    public NavigationHeader(Activity activity, NavigationView navigationView) {
        this.mActivity = activity;
        View headView = navigationView.getHeaderView(0);
        headView.findViewById(R.id.textView_login).setOnClickListener(this);
        headView.findViewById(R.id.textView_signup).setOnClickListener(this);
        mImageViewAvatar = (ImageView) headView.findViewById(R.id.imageView_avatar);
        mTextViewNickName = (TextView) headView.findViewById(R.id.textView_nickName);
    }


    public void bindData() {
//        OauthUserEntity oauthUserEntity = App.getInstance().getOauthUserEntity();
//        if (oauthUserEntity != null) {
//            mImageViewAvatar.setOnClickListener(this);
//            mRelativeLayout1.setVisibility(View.GONE);
//            mRelativeLayout2.setVisibility(View.VISIBLE);
//            SimpleUserEntity entity = oauthUserEntity.getUser();
//            mTextViewNickName.setText(entity.getScreen_name());
//            mTextViewVideosCount.setText(entity.getVideos_count() + "\n" + mActivity.getString(R.string.video));
//            mTextViewRepostsCount.setText(entity.getReposts_count() + "\n" + mActivity.getString(R.string.reposts));
//            mTextViewFriendsCount.setText(entity.getFriends_count() + "\n" + mActivity.getString(R.string.following));
//            mTextViewFollowersCount.setText(entity.getFollowers_count() + "\n" + mActivity.getString(R.string.following));
//            if (TextUtils.isEmpty(entity.getAvatar()))
//                return;
//            AppUtils.loadBigUserAvata(mActivity, entity, mImageViewAvatar);
//        } else {
        mImageViewAvatar.setOnClickListener(this);
        mTextViewNickName.setText(R.string.welcome);
        mImageViewAvatar.setImageResource(R.drawable.ic_account_circle_white_48dp);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_login:
//                mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                break;
            case R.id.textView_signup:
//                mActivity.startActivity(new Intent(mActivity, SignUpActivity.class));
                break;
            case R.id.imageView_avatar:
                mActivity.startActivity(new Intent(mActivity, UserInfoActivity.class));
                break;
        }
    }
}
