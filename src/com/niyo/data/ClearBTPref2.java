package com.niyo.data;

import com.niyo.SettingsManager;
import com.niyo.auto.AppLauncher;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

public class ClearBTPref2 extends Preference {

	public ClearBTPref2(Context context, AttributeSet attrs) {
		super(context, attrs);
		
//		setWidgetLayoutResource(R.layout.pref_widget);
	}
	

	@Override
    protected void onClick() {
		
		SettingsManager.removeSet(getContext(), AppLauncher.BT_MAC_ADDRESS_SET);
    }
	
}
