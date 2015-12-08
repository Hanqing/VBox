package com.smart.vbox.ui.activity.user;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.smart.vbox.R;
import com.smart.vbox.support.utils.ViewUtils;
import com.smart.vbox.ui.activity.BaseActivity;
import com.smart.vbox.ui.fragment.UserInfoEditFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author lhq
 *         created at 2015/12/5 15:32
 */
public class UserInfoEditActivity extends BaseActivity {
    private UserInfoEditFragment mUserInfoEditFragment;

    @Bind(R.id.tool_bar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);
        ButterKnife.bind(this);

        ViewUtils.setToolbarAsBack(this, mToolbar, getString(R.string.title_edit_user_info));

        if (savedInstanceState == null) {
            replaceFragment(R.id.view_holder, mUserInfoEditFragment = UserInfoEditFragment.newInatance());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUserInfoEditFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            mUserInfoEditFragment.save();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void replaceFragment(int viewId, android.app.Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(viewId, fragment).commit();
    }


}
