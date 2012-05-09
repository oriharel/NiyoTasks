package com.niyo.auto;

import com.niyo.ClientLog;
import com.niyo.SettingsManager;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AppLauncher extends BroadcastReceiver {

	private static final String LOG_TAG = AppLauncher.class.getSimpleName();
	public static final String BT_MAC_ADDRESS = "btMacAddress";
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		ClientLog.d(LOG_TAG, "recieved AppLauncher with "+intent.getAction());
		Bundle deviceInfo = intent.getExtras();
		Object extraDevice = deviceInfo.get(BluetoothDevice.EXTRA_DEVICE);
		ClientLog.d(LOG_TAG, "device is "+extraDevice);
		Object extraName = deviceInfo.get(BluetoothDevice.EXTRA_NAME);
		ClientLog.d(LOG_TAG, "name is "+extraName);
		
		String storedMacAddress = SettingsManager.getString(context, BT_MAC_ADDRESS);
		ClientLog.d(LOG_TAG, "stored mac is "+storedMacAddress);
		if (storedMacAddress != null && storedMacAddress.toString().equals(extraDevice.toString())){
			Intent intent2Launch = new Intent(context, AutoActivity.class);
			intent2Launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent2Launch);
		}
		else{
			Intent pairIntent = BtPairActivity.getCreationIntent(context, extraDevice.toString());
			pairIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(pairIntent);
		}
		
		
	}

}
