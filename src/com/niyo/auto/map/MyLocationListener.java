package com.niyo.auto.map;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {
	
	LocationManager mManager;
	public MyLocationListener(Context context)
	{
		String locationContext = Context.LOCATION_SERVICE;
		mManager = (LocationManager)context.getSystemService(locationContext);
	}

	@Override
	public void onLocationChanged(Location location) {
		
		mManager.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
