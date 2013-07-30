package com.niyo.radar;

import com.niyo.ClientLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RadarBroadcastReceiver extends BroadcastReceiver {
	
	public static final String FRIEND_LAT = "friend_lat";
	public static final String FRIEND_LON = "friend_lon";
	public static final String FRIEND_EMAIL = "friend_email";
	public static final String FRIEND_UPDATE_TIME = "friend_update_time";
	public static final String FRIENDS_IMAGE_URL = "friend_image_url";
	private RadarActivity mRadarActivity;
	private static final String LOG_TAG = RadarBroadcastReceiver.class.getSimpleName();
	
	public RadarBroadcastReceiver(RadarActivity radarActivity) {
		
		mRadarActivity = radarActivity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		ClientLog.d(LOG_TAG, "received intent");
		Double lat = intent.getDoubleExtra(FRIEND_LAT, 0);
		Double lon = intent.getDoubleExtra(FRIEND_LON, 0);
		String email = intent.getStringExtra(FRIEND_EMAIL);
		String updateMsg = intent.getStringExtra(FRIEND_UPDATE_TIME);
		String imageUrl = intent.getStringExtra(FRIENDS_IMAGE_URL);
		
		mRadarActivity.updateFriend(email, lat, lon, updateMsg, imageUrl);

	}

}
