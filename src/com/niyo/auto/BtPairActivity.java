package com.niyo.auto;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.SettingsManager;

public class BtPairActivity extends NiyoAbstractActivity {

	private static final String BT_INFO_EXTRA = "btInfo";
	private static final String LOG_TAG = BtPairActivity.class.getSimpleName();

	@Override
	public void onPause() {
		super.onPause();
//		unregisterReceiver(receiver);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.bt_pair_layout);
		final TextView info = (TextView)findViewById(R.id.deviceMacAddress);
		info.setText(getIntent().getStringExtra(BT_INFO_EXTRA));
		
//		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_NAME_CHANGED);
		BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		final BluetoothDevice device = mAdapter.getRemoteDevice(getIntent().getStringExtra(BT_INFO_EXTRA));
		if (device != null && device.getName() != null) {
			info.setText(device.getName());
		}
		ClientLog.d(LOG_TAG, "device is "+device.getName()+" for address "+getIntent().getStringExtra(BT_INFO_EXTRA));
//		registerReceiver(new BroadcastReceiver(){
//
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				ClientLog.d(LOG_TAG, "got action name changed");
//				info.setText(intent.getStringExtra(BluetoothDevice.EXTRA_NAME));
//				
//			}
//			
//		}, filter);
		
		findViewById(R.id.btNo).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
		
		findViewById(R.id.btYes).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SettingsManager.addToStringSet(BtPairActivity.this, AppLauncher.BT_MAC_ADDRESS_SET,
						AppLauncher.BT_MAC_ADDRESS_SET, device.getName());
				Intent intent2Launch = new Intent(BtPairActivity.this, AutoActivity.class);
				intent2Launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				BtPairActivity.this.startActivity(intent2Launch);
				finish();
			}
		});
		
	}
	
	public static Intent getCreationIntent(Context activity, String btInfo){
		
		Intent intent = new Intent(activity, BtPairActivity.class);
		intent.putExtra(BT_INFO_EXTRA, btInfo);
		return intent;
	}
}
