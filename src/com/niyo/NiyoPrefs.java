package com.niyo;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class NiyoPrefs extends PreferenceFragment {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.niyo_prefs);
        getPreferenceManager().setSharedPreferencesName(SettingsManager.PREFERENCES_FILE_NAME);
        
    }
}
