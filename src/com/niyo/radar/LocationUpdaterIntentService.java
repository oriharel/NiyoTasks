package com.niyo.radar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.niyo.ClientLog;
import com.niyo.ServiceCaller;
import com.niyo.SettingsManager;
import com.niyo.network.GenericHttpRequestTask;
import com.niyo.network.NetworkUtilities;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

public class LocationUpdaterIntentService extends IntentService implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{
	
	private static final String LOG_TAG = LocationUpdaterIntentService.class.getSimpleName();
	public static final String USER_ASKING_PROPERTY = "user_asking";
	public static final String TRX_ID_PROPERTY = "trx_id";
	private LocationClient mLocationClient;
	private String mUserAsking;
	private String mTrxId;

	public LocationUpdaterIntentService() {
		super("locationUpdater");
		
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		mUserAsking = intent.getStringExtra(USER_ASKING_PROPERTY);
		mTrxId = intent.getStringExtra(TRX_ID_PROPERTY);
		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();
		
		
		ClientLog.d(LOG_TAG, "handle intent with userAsking "+mUserAsking);

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		
		Location location = mLocationClient.getLastLocation();
		long updateTime = location.getTime();
		String userAnswering = SettingsManager.getString(this, RadarActivity.USER_EMAIL);
		ServiceCaller caller = new ServiceCaller() {
			
			@Override
			public void success(Object data) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void failure(Object data, String description) {
				// TODO Auto-generated method stub
				
			}
		};
		GenericHttpRequestTask task = new GenericHttpRequestTask(caller);
		String imageUrl = SettingsManager.getString(this, GoogleAuthTask.PROFILE_IMAGE_URL);
		
		String url = NetworkUtilities.BASE_URL+"/answerPosition?user_asking="+mUserAsking+
				"&user_answering="+userAnswering+
				"&latitude="+location.getLatitude()+
				"&longitude="+location.getLongitude()+
				"&update_time="+updateTime+
				"&image_url="+imageUrl+
				"&trx_id="+mTrxId;
		
		ClientLog.d(LOG_TAG, "sending location update with url "+url);
		
		task.execute(url);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

}
