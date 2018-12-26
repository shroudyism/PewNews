package com.example.shroudyism.pewnews;

import android.os.Bundle;
import android.preference.Preference;
import android.support.v14.preference.PreferenceFragment;

import com.example.shroudyism.wallpaperfinder.R;

public class SettingsFragments extends PreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
addPreferencesFromResource(R.xml.pref_vis);
    }
}
