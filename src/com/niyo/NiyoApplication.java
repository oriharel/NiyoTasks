package com.niyo;

import java.util.ArrayList;

import com.niyo.data.JsonFetchIntentService;

import android.app.Application;
import android.content.Intent;

public class NiyoApplication extends Application {

	private static final String LOG_TAG = NiyoApplication.class.getSimpleName();
	private static Boolean s_logEnabled = true;
	public static boolean isLogEnabled() {
		return s_logEnabled;
	}
	
	@Override
	public void onCreate(){
		
		super.onCreate();
		fetchData();
	}
	
	public void fetchData(){
		ArrayList<String> urls = new ArrayList<String>();
		try {
			urls.add("http://niyoapi.appspot.com/categories");
			urls.add("http://niyoapi.appspot.com/tasks");
			urls.add("http://niyoapi.appspot.com/getFlatTasks");
			
			Intent intent = new Intent(this, JsonFetchIntentService.class);
			intent.putStringArrayListExtra(JsonFetchIntentService.URLS_EXTRA, urls);
			startService(intent);
			
		} catch (Exception e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
	}

}
