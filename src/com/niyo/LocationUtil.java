package com.niyo;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.niyo.auto.map.MyLocationListener;

public class LocationUtil {
	
	private static final String LOG_TAG = LocationUtil.class.getSimpleName();
	
	public static void updateLocation(Context context, MyLocationListener gpsListener, MyLocationListener networkListener, MyLocationListener passiveListener){
		
		ClientLog.d(LOG_TAG, "updateLocation started");
		String locationContext = Context.LOCATION_SERVICE;
		final LocationManager locationManager = (LocationManager)context.getSystemService(locationContext);
		
		String gps = LocationManager.GPS_PROVIDER;
		String network = LocationManager.GPS_PROVIDER;
		String passive = LocationManager.GPS_PROVIDER;
		
		int t = 5000; // milliseconds
		int distance = 5; // meters
		
		
		if (gpsListener != null) {
			ClientLog.d(LOG_TAG, "requesting gps");
			locationManager.requestLocationUpdates(gps, t, distance,
					gpsListener);
		}
		
		
		if (networkListener != null) {
			ClientLog.d(LOG_TAG, "requesting network");
			locationManager.requestLocationUpdates(network, t, distance,
					networkListener);
		}
		
		if (passiveListener != null) {
			ClientLog.d(LOG_TAG, "requesting passive");
			locationManager.requestLocationUpdates(passive, t, distance,
					passiveListener);
		}
		
		
	}
	
	public static Location getLocation(Context context){
		
		String serviceString = Context.LOCATION_SERVICE;
		LocationManager locationManager;
		locationManager = (LocationManager)context.getSystemService(serviceString);
		
		String provider = LocationManager.GPS_PROVIDER;
		Location location = locationManager.getLastKnownLocation(provider);
		
		
		if (location == null){
			provider = LocationManager.NETWORK_PROVIDER;
			location = locationManager.getLastKnownLocation(provider);
		}
		
		if (location == null){
			provider = LocationManager.PASSIVE_PROVIDER;
			location = locationManager.getLastKnownLocation(provider);
		}
		
		return location;
	}

	public static void removeLocationUpdates(Context context, MyLocationListener gpsListener, MyLocationListener networkListener, MyLocationListener passiveListener) {
		String locationContext = Context.LOCATION_SERVICE;
		final LocationManager locationManager = (LocationManager)context.getSystemService(locationContext);
		
		if (gpsListener != null) {
			locationManager.removeUpdates(gpsListener);
		}
		
		if (networkListener != null) {
			locationManager.removeUpdates(networkListener);
		}
		
		if (passiveListener != null) {
			locationManager.removeUpdates(passiveListener);
		}
		
		
		
		
	}
}
