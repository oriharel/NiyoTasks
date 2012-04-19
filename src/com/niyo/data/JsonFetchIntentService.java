package com.niyo.data;

import java.net.URL;
import java.util.List;

import com.niyo.ClientLog;

import android.app.IntentService;
import android.content.Intent;

public class JsonFetchIntentService extends IntentService {

	public static final String URLS_EXTRA = "urls";
	private static final String LOG_TAG = JsonFetchIntentService.class.getSimpleName();
	
	public JsonFetchIntentService() {
		super("jsons");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		List<String> urls = (List<String>)intent.getSerializableExtra(URLS_EXTRA);
		
		for (String urlStr : urls) {
			
			ClientLog.d(LOG_TAG, "sending url "+urlStr);
			GetJSONTask task = new GetJSONTask(getApplicationContext());
			try {
				task.execute(new URL(urlStr));
			} catch (Exception e) {
				ClientLog.e(LOG_TAG, "Error!", e);
			}
		}
		
	}

}
