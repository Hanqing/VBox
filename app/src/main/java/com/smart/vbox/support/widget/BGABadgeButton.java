package com.smart.vbox.support.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Checkable;

import com.smart.vbox.R;

import cn.bingoogolapple.badgeview.BGABadgeViewHelper;
import cn.bingoogolapple.badgeview.BGABadgeable;
import cn.bingoogolapple.badgeview.BGADragDismissDelegate;

/**
 * Created by Hanqing on 2015/12/17.
 * 增加android:checked属性设置
 */
public class BGABadgeButton extends Button implements BGABadgeable, Checkable {
    private BGABadgeViewHelper mBadgeViewHeler;

    private OnCheckedChangeListener onCheckedChangeListener;


    public BGABadgeButton(Context context) {
        this(context, null);
    }

    public BGABadgeButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public BGABadgeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setChecked(attrs);
        mBadgeViewHeler = new BGABadgeViewHelper(this, context, attrs, BGABadgeViewHelper.BadgeGravity.RightTop);
    }

    private void setChecked(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CheckableButton);
        setChecked(a.getBoolean(R.styleable.CheckableButton_android_checked, false));
        a.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mBadgeViewHeler.onTouchEvent(event);
    }

    @Override
    public boolean callSuperOnTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBadgeViewHeler.drawBadge(canvas);
    }

    @Override
    public boolean isChecked() {
        return isSelected();
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    public void setChecked(boolean checked) {
        setSelected(checked);

        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, checked);
        }
    }

    @Override
    public void showCirclePointBadge() {
        mBadgeViewHeler.showCirclePointBadge();
    }

    @Override
    public void showTextBadge(String badgeText) {
        mBadgeViewHeler.showTextBadge(badgeText);
    }

    @Override
    public void hiddenBadge() {
        mBadgeViewHeler.hiddenBadge();
    }

    @Override
    public void showDrawableBadge(Bitmap bitmap) {
        mBadgeViewHeler.showDrawable(bitmap);
    }

    @Override
    public void setDragDismissDelegage(BGADragDismissDelegate delegate) {
        mBadgeViewHeler.setDragDismissDelegage(delegate);
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public static interface OnCheckedChangeListener {
        void onCheckedChanged(BGABadgeButton buttonView, boolean isChecked);
    }
}
