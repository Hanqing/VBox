package com.smart.vbox.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.smart.vbox.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.badgeview.BGABadgeTextView;

/**
 * Created by Hanqing on 2015/12/21.
 */
public class TestActivity extends AppCompatActivity {

    private String[] mVals = new String[]
            {"Hello", "Android", "Weclome Hi ", "Button", "TextView", "Hello",
                    "Android", "Weclome", "Button ImageView", "TextView", "Helloworld",
                    "Android", "Weclome Hello", "Button Text", "TextView"};

    @Bind(R.id.episode_flowlayout)
    TagFlowLayout mEpisodeFlow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        initEpisodeFlow();
    }

    /**
     * 初始化集数显示
     */
    private void initEpisodeFlow() {
        final LayoutInflater mInflater = LayoutInflater.from(this);

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
                Toast.makeText(TestActivity.this, mVals[position], Toast.LENGTH_SHORT).show();
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
}
