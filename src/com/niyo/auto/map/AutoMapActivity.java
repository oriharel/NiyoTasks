package com.niyo.auto.map;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.auto.AutoPoint;
import com.niyo.auto.AutoWVClient;
import com.niyo.auto.JSInterface;

public class AutoMapActivity extends NiyoAbstractActivity {
	
	private static final String LOG_TAG = AutoMapActivity.class.getSimpleName();
	private static final String TO_EXTRA = "toExtra";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.auto_map_layout);
		
		initWebView();
	}

	private void initWebView() {
		
		WebView webView = (WebView)findViewById(R.id.autoMapWebView);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLoadWithOverviewMode(true);
		settings.setUseWideViewPort(true);
		settings.setDomStorageEnabled(true);
		webView.setWebViewClient(new AutoWVClient(this, getTo()));
		JSInterface inter = new JSInterface();
		webView.addJavascriptInterface(inter, "Native");
		webView.setWebChromeClient(new WebChromeClient() 
		{
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) 
			{
				return super.onJsAlert(view, url, message, result);
		    }
		});
		
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setAppCacheEnabled(true);
        webView.loadUrl("file:///android_asset/html/test.html");
		
	}
	
	private AutoPoint getTo(){
		return (AutoPoint)getIntent().getSerializableExtra(TO_EXTRA);
	}
	
	public static Intent getCreationIntent(Activity activity, AutoPoint to){
		
		Intent intent = new Intent(activity, AutoMapActivity.class);
		intent.putExtra(TO_EXTRA, to);
		
		return intent;
	}
	
	public void navigateTo(View view) 
	{
    	String locationStr = (String)view.getTag();
    	String[] coordinsatesStrArray = locationStr.split(",");
    	String geoQuery = "geo:"+coordinsatesStrArray[0]+","+coordinsatesStrArray[1];
    	Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(geoQuery)); 
    	if (isCallable(intent))
    	{
    		startActivity(intent);
    	}
    	else
    	{
    		Toast.makeText(this, "Go get yourself a map app", Toast.LENGTH_LONG).show();
    	}
	}
	
	private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 
            PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
	
	

}