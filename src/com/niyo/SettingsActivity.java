package com.niyo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SettingsActivity extends Activity {

	private static final String LOG_TAG = SettingsActivity.class.getSimpleName();
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        ClientLog.d(LOG_TAG, "onCreate started");
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new NiyoPrefs())
                .commit();
        
        setTitle("Settings");
    }

	public static Intent getCreationIntent(Activity autoActivity) {
		
		Intent intent = new Intent(autoActivity, SettingsActivity.class);
		return intent;
	}
}
