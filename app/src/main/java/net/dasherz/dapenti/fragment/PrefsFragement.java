package net.dasherz.dapenti.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import net.dasherz.dapenti.R;
import net.dasherz.dapenti.util.LogUtil;

/**
* Created by gary on 2015/4/14.
*/
public class PrefsFragement extends PreferenceFragment {
    private static final String TAG = PrefsFragement.class.getSimpleName();
    public PrefsFragement() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        LogUtil.d(TAG, this.getPreferenceManager().getSharedPreferencesName());
    }

}
