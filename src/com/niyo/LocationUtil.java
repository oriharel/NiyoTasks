package com.niyo;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.niyo.auto.map.MyLocationListener;

public class LocationUtil {
	
	public static void updateLocation(Context context){
		
		String locationContext = Context.LOCATION_SERVICE;
		final LocationManager locationManager = (LocationManager)context.getSystemService(locationContext);
		
		String gps = LocationManager.GPS_PROVIDER;
		String network = LocationManager.GPS_PROVIDER;
		String passive = LocationManager.GPS_PROVIDER;
		
		int t = 5000; // milliseconds
		int distance = 5; // meters
		
		MyLocationListener gpsListener = new MyLocationListener(context);
		MyLocationListener networkListener = new MyLocationListener(context);
		MyLocationListener passiveListener = new MyLocationListener(context);
		
		locationManager.requestLocationUpdates(gps, t, distance,
				gpsListener);
		locationManager.requestLocationUpdates(network, t, distance,
				networkListener);
		locationManager.requestLocationUpdates(passive, t, distance,
				passiveListener);
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
}
