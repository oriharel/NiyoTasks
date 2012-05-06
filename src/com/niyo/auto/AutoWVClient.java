package com.niyo.auto;

import android.content.Context;
import android.location.Location;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.niyo.ClientLog;
import com.niyo.LocationUtil;

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
		
		Location location = LocationUtil.getLocation(mContext);
		
		ClientLog.d(LOG_TAG, "location is "+location);
		
		if (location != null){
			AutoPoint from = new AutoPoint(location.getLatitude(), location.getLongitude(), "origin");
			
			webView.loadUrl("javascript: calcRoute("+from.getLat()+","+from.getLon()+","+mTo.getLat()+","+mTo.getLon()+")");
		}
		
	}
	
}
