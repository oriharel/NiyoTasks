package com.niyo.auto;

import com.niyo.ClientLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppLauncher extends BroadcastReceiver {

	private static final String LOG_TAG = AppLauncher.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		ClientLog.d(LOG_TAG, "recieved AppLauncher with "+intent.getAction());
		
		Intent intent2Launch = new Intent(context, AutoActivity.class);
		intent2Launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent2Launch);
	}

}
