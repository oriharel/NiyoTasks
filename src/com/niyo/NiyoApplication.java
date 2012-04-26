package com.niyo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.content.Intent;

import com.niyo.data.JsonFetchIntentService;

public class NiyoApplication extends Application {

	private static final String LOG_TAG = NiyoApplication.class.getSimpleName();
	private static Boolean s_logEnabled = true;
	private JSONArray mFoursqaureVenues;
	public static boolean isLogEnabled() {
		return s_logEnabled;
	}
	
	@Override
	public void onCreate(){
		
		super.onCreate();
		fetchData();
		fetchFoursquareVenues();
	}
	
	private void fetchFoursquareVenues() {

		ClientLog.d(LOG_TAG, "reading the categories");
		try {        
			BufferedReader in = null;
			in = new BufferedReader(new InputStreamReader(getAssets().open("categories.json")));
			String line;
			StringBuilder sb = new StringBuilder();
			
			while((line =in.readLine()) != null){
				sb.append(line);
			}
			
			in.close();
			//create a json object
			JSONObject json = new JSONObject(sb.toString());

			//loop over the items array and look for pid
			JSONArray categories = json.getJSONObject("response").getJSONArray("categories");
			setFoursqaureVenues(categories);
			
			//TODO - to remove. expensinve loop
			for (int i = 0; i < getFoursqaureVenues().length(); i++){
				ClientLog.d(LOG_TAG, "category: "+getFoursqaureVenues().getJSONObject(i).getString("name"));
			}
			
		} catch (Exception e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}


	}

	public void fetchData(){
		ArrayList<String> urls = new ArrayList<String>();
		try {
			urls.add("http://niyoapi.appspot.com/categories");
			urls.add("http://niyoapi.appspot.com/tasks");
			urls.add("http://niyoapi.appspot.com/getFlatTasks");
			urls.add("https://api.foursquare.com/v2/venues/categories?oauth_token=3MU3QXE3H3KHT33DGG4NMR0KD221DVFMOFQQQ3VOIUQ5DKJY&v=20120424");
			
			Intent intent = new Intent(this, JsonFetchIntentService.class);
			intent.putStringArrayListExtra(JsonFetchIntentService.URLS_EXTRA, urls);
			startService(intent);
			
		} catch (Exception e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
	}

	public JSONArray getFoursqaureVenues() {
		return mFoursqaureVenues;
	}

	public void setFoursqaureVenues(JSONArray foursqaureVenues) {
		mFoursqaureVenues = foursqaureVenues;
	}

}
