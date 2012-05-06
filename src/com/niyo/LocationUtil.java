package com.niyo;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationUtil {

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
