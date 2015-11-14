package com.animbus.music.ui.activity.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.animbus.music.R;

/**
 * Created by Adrian on 11/1/2015.
 */
public class PrefsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.prefs);
    }
}
