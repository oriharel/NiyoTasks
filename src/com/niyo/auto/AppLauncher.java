package com.niyo.auto;

import java.util.Set;

import com.niyo.ClientLog;
import com.niyo.SettingsManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class AppLauncher extends BroadcastReceiver {

	private static final String LOG_TAG = AppLauncher.class.getSimpleName();
	public static final String BT_MAC_ADDRESS_SET = "btMacAddressSet";
	public static final String BT_MAC_ADDRESS_SET_NO = "btMacAddressSetNo";
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		ClientLog.d(LOG_TAG, "recieved AppLauncher with "+intent.getAction());
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		Boolean btEnabled = sharedPref.getBoolean("niyo_bt_enable", true);
		
		if (!btEnabled) {
			return;
		}
		
		Bundle deviceInfo = intent.getExtras();
		Object extraDevice = deviceInfo.get(BluetoothDevice.EXTRA_DEVICE);
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice device = adapter.getRemoteDevice(extraDevice.toString());
		ClientLog.d(LOG_TAG, "device is "+extraDevice);
		Object extraName = deviceInfo.get(BluetoothDevice.EXTRA_NAME);
		ClientLog.d(LOG_TAG, "name is "+extraName);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Set<String> storedMacAddressSet = SettingsManager.getStringSet(context, BT_MAC_ADDRESS_SET);
			
			Set<String> storedMacAddressSetNo = SettingsManager.getStringSet(context, BT_MAC_ADDRESS_SET_NO);
			
			if (storedMacAddressSetNo != null) {
				ClientLog.d(LOG_TAG, "NO set has "+storedMacAddressSetNo.size()+" items");
				for (String storedMacAddress : storedMacAddressSetNo) {
					ClientLog.d(LOG_TAG, "NO stored mac is "+storedMacAddress);
					if (storedMacAddress != null && storedMacAddress.toString().equals(device.getName())){
						ClientLog.d(LOG_TAG, "this bt device is black listed");
						return;
					}
				}
			}
			
			
			if (storedMacAddressSet != null) {
				ClientLog.d(LOG_TAG, "set has "+storedMacAddressSet.size()+" items");
				for (String storedMacAddress : storedMacAddressSet) {
					ClientLog.d(LOG_TAG, "stored mac is "+storedMacAddress);
					if (storedMacAddress != null && storedMacAddress.toString().equals(device.getName())){
						Intent intent2Launch = new Intent(context, AutoActivity.class);
						intent2Launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent2Launch);
						return;
					}
				}
			}
	    }
		else {
			String storedMacAddress = SettingsManager.getString(context, BT_MAC_ADDRESS_SET);
			
			
			
//			if (storedMacAddressSet != null) {
//				ClientLog.d(LOG_TAG, "set has "+storedMacAddressSet.size()+" items");
//				for (String storedMacAddress : storedMacAddressSet) {
					ClientLog.d(LOG_TAG, "stored mac is "+storedMacAddress);
					if (storedMacAddress != null && storedMacAddress.toString().equals(device.getName())){
						Intent intent2Launch = new Intent(context, AutoActivity.class);
						intent2Launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent2Launch);
						return;
					}
//				}
//			}
		}
		
		
		
		
		Intent pairIntent = BtPairActivity.getCreationIntent(context, extraDevice.toString());
		pairIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(pairIntent);
		
		
		
	}

}
