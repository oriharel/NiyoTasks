package com.niyo.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppLauncher extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Intent intent2Launch = new Intent(context, AutoActivity.class);
		intent2Launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent2Launch);
	}

}
