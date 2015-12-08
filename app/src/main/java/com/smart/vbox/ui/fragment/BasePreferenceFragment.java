package com.smart.vbox.ui.fragment;

import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 *
 * @author lhq
 * created at 2015/12/5 15:35
 */
public class BasePreferenceFragment extends PreferenceFragment {


    public <T extends Preference> T findPreference(String key) {
        return (T) getPreferenceScreen().findPreference(key);
    }

    public <T extends Preference> T findPreference(int keyRes) {
        return findPreference(getString(keyRes));
    }


}
