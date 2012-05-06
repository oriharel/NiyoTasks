package com.niyo.auto;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.niyo.ClientLog;

public class AutoWVClient extends WebViewClient {

	private static final String LOG_TAG = AutoWVClient.class.getSimpleName();
	private Context mContext;
	private AutoPoint mTo;
	
	public AutoWVClient(Context context, AutoPoint to){
		
		mContext = context;
		mTo = to;
		
	}
	
	@Override
	public void onPageFinished(WebView webView, String url) 
	{
		processRoute(webView);
		
	}
	
	private void processRoute(WebView webView) {
		
		ClientLog.d(LOG_TAG, "processing route");
		String serviceString = Context.LOCATION_SERVICE;
		LocationManager locationManager;
		locationManager = (LocationManager)mContext.getSystemService(serviceString);
		
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
		
		ClientLog.d(LOG_TAG, "location is "+location);
		
		if (location != null){
			AutoPoint from = new AutoPoint(location.getLatitude(), location.getLongitude(), "origin");
			
			webView.loadUrl("javascript: calcRoute("+from.getLat()+","+from.getLon()+","+mTo.getLat()+","+mTo.getLon()+")");
		}
		
	}
	
}
