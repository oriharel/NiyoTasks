package com.niyo.categories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;

import com.niyo.ClientLog;
import com.niyo.ServiceCaller;
import com.niyo.data.DBJsonFetchTask;

public class CategoriesJsonFetchTask extends DBJsonFetchTask {
	
	private static final String LOG_TAG = CategoriesJsonFetchTask.class.getSimpleName();
	
	public CategoriesJsonFetchTask(Context context, ServiceCaller caller) {
		super(context, caller);
	}
	
	@Override
	protected JSONObject doInBackground(Uri... params) {
		
		JSONObject categoriesJson = super.doInBackground(params);
		
		try {
			
			JSONArray categories = categoriesJson.getJSONArray("categories");
			for (int i = 0; i < categories.length(); i++){
				
			}
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		
		return null;
	}

}
