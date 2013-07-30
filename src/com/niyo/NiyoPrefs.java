package com.niyo;

import com.niyo.auto.AutoActivity;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class NiyoPrefs extends PreferenceFragment {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.niyo_prefs);
//        getPreferenceManager().setSharedPreferencesName(SettingsManager.PREFERENCES_FILE_NAME);
//        getPreferenceScreen().getPreference(0).setDefaultValue(SettingsManager.getString(getActivity(), AutoActivity.USE_GOOGLE_MAPS));
//        getPreferenceScreen().getPreference(1).setDefaultValue("niyo_bt_enable");
        
    }
}
