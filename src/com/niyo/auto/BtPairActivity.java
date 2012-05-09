package com.niyo.auto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.SettingsManager;

public class BtPairActivity extends NiyoAbstractActivity {

	private static final String BT_INFO_EXTRA = "btInfo";

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.bt_pair_layout);
		TextView info = (TextView)findViewById(R.id.deviceMacAddress);
		info.setText(getIntent().getStringExtra(BT_INFO_EXTRA));
		
		findViewById(R.id.btNo).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
		
		findViewById(R.id.btYes).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SettingsManager.setString(BtPairActivity.this, AppLauncher.BT_MAC_ADDRESS, getIntent().getStringExtra(BtPairActivity.BT_INFO_EXTRA));
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
