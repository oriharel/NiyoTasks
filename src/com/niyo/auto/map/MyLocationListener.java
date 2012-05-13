package com.niyo.auto.map;

import com.niyo.ClientLog;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {
	
	private static final String LOG_TAG = MyLocationListener.class.getSimpleName();
	LocationManager mManager;
	private String mProvider;
	public MyLocationListener(Context context, String provider)
	{
		String locationContext = Context.LOCATION_SERVICE;
		mManager = (LocationManager)context.getSystemService(locationContext);
		mProvider = provider;
	}

	@Override
	public void onLocationChanged(Location location) {
		
		ClientLog.d(LOG_TAG, "onLocationChanged called from "+mProvider);
		mManager.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		
		ClientLog.d(LOG_TAG, "onProviderDisabled called from "+mProvider);

	}

	@Override
	public void onProviderEnabled(String provider) {
		
		ClientLog.d(LOG_TAG, "onProviderEnabled called from "+mProvider);

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
		ClientLog.d(LOG_TAG, "onStatusChanged called from "+mProvider);

	}

}
