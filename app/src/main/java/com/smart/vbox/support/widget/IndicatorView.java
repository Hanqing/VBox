package com.smart.vbox.support.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smart.vbox.R;

/**
 * Created by Hanqing on 2015/12/26.
 */
public class IndicatorView extends LinearLayout {

    TextView mTitle;

    public IndicatorView(Context context) {
        super(context);
        init();
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.indicator_title_bar, this);
        mTitle = (TextView) view.findViewById(R.id.tv_title);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }
}
