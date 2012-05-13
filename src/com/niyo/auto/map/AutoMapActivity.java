package com.niyo.auto.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.auto.AutoPoint;
import com.niyo.auto.AutoVenue;
import com.niyo.auto.AutoWVClient;
import com.niyo.auto.JSInterface;

public class AutoMapActivity extends NiyoAbstractActivity {
	
	private static final String LOG_TAG = AutoMapActivity.class.getSimpleName();
	private static final String TO_EXTRA = "toExtra";
	private static final String CATEGORY_IDS_EXTRA = "idsExtra";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.auto_map_layout);
		Button navBtn = (Button)findViewById(R.id.startNavBtn);
		navBtn.setText("Start navigating to "+getDesName());
		findViewById(R.id.startNavBtn).setOnClickListener(getStartNavOnClick());
		initWebView();
	}

	private CharSequence getDesName() {
		AutoPoint to = (AutoPoint)getIntent().getSerializableExtra(TO_EXTRA);
		return to.getName();
	}

	private OnClickListener getStartNavOnClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				navigateTo(v);
				
			}
		};
	}

	private void initWebView() {
		
		WebView webView = (WebView)findViewById(R.id.autoMapWebView);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setUseWideViewPort(true);
		
		final int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
    	ClientLog.d(LOG_TAG, "sdk is "+sdkVersion);
    	if (sdkVersion >= Build.VERSION_CODES.ECLAIR_MR1) {
//    		settings.setDomStorageEnabled(true);
//    		settings.setLoadWithOverviewMode(true);
    	}
		
		
		webView.setWebViewClient(new AutoWVClient(this, getTo()));
		JSInterface inter = new JSInterface(this, getCategryIdsToTaskContent());
		webView.addJavascriptInterface(inter, "Native");
		webView.setWebChromeClient(new WebChromeClient() 
		{
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) 
			{
				return super.onJsAlert(view, url, message, result);
		    }
		});
		
        webView.loadUrl("file:///android_asset/html/test.html");
		
	}
	
	private AutoPoint getTo(){
		return (AutoPoint)getIntent().getSerializableExtra(TO_EXTRA);
	}
	
	private Map<String, String> getCategryIdsToTaskContent(){
		return (Map<String, String>)getIntent().getSerializableExtra(CATEGORY_IDS_EXTRA);
	}
	
	
	public static Intent getCreationIntent(Activity activity, AutoPoint to, HashMap<String, String> categroyIds){
		
		Intent intent = new Intent(activity, AutoMapActivity.class);
		intent.putExtra(TO_EXTRA, to);
		intent.putExtra(CATEGORY_IDS_EXTRA, categroyIds);
		
		return intent;
	}
	
	public void navigateTo(View view) 
	{
		AutoPoint point = getTo();
    	String geoQuery = "geo:"+point.getLat()+","+point.getLon();
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

	public void showRelevantVenues(List<AutoVenue> result) 
	{
		Toast.makeText(this, "there are close venues which you have tasks to do in", Toast.LENGTH_SHORT).show();
		JSONArray venues = new JSONArray();
		
		for (AutoVenue autoVenue : result) {
			try {
				JSONObject venue = new JSONObject("{lat:"+autoVenue.getLocation().getLat()+
						", long:"+autoVenue.getLocation().getLon()+", name:\""+autoVenue.getName()+"\", taskContent:\""+autoVenue.getTaskContent()+"\"}");
				venues.put(venue);
				
				
				
			} catch (JSONException e) {
				ClientLog.e(LOG_TAG, "Error!", e);
			}
		}
		
		WebView webView = (WebView)findViewById(R.id.autoMapWebView);
		ClientLog.d(LOG_TAG, "sending to webview "+venues);
		if (venues.length() > 0){
			webView.loadUrl("javascript: addVenues("+venues+")");
		}
		else{
			ClientLog.e(LOG_TAG, "Error!, for some reason got 0 venues for the map");
		}
		
	}
	
	

}
