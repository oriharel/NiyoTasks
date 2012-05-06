package com.niyo.auto;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

import com.niyo.ClientLog;
import com.niyo.ServiceCaller;
import com.niyo.StringUtils;
import com.niyo.Utils;
import com.niyo.auto.map.AutoMapActivity;
import com.niyo.data.DBJsonFetchTask;
import com.niyo.data.NiyoContentProvider;

public class JSInterface {
	
	private static final String LOG_TAG = JSInterface.class.getSimpleName();
	private List<String> mCategoryIds;
	private AutoMapActivity mActivity;
	
	public JSInterface(List<String> categoryIds, AutoMapActivity activity){
		
		mCategoryIds = categoryIds;
		mActivity = activity;
	}
	
	public void log(String text){
		ClientLog.d(LOG_TAG, "from webview "+text);
	}
	
	public void setResponse(String response, String fromLat, String fromLon, String toLat, String toLon){
		Log.d(LOG_TAG, "response is "+response);
		try {
			
			JSONObject directionsJson = new JSONObject(response);
			JSONArray routes = directionsJson.getJSONArray("routes");
			Log.d(LOG_TAG, "num of routes is "+routes.length());
			JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
			Log.d(LOG_TAG, "num of legs is "+legs.length());
			JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");
			Log.d(LOG_TAG, "num of steps is "+steps.length());
			
			if (mCategoryIds != null && mCategoryIds.size() > 0){
				populateFoursquareVenues(fromLat, fromLon, toLat, toLon, legs, steps);
			}
			
			
			populateLocationTasks(steps);
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "Error!", e);
		}
	}

	private void populateLocationTasks(final JSONArray steps) {
		
		ServiceCaller caller = new ServiceCaller() {
			
			@Override
			public void success(Object data) {
				
				if (data != null){
					
					JSONObject tasksJson = (JSONObject)data;
					JSONArray tasks;
					try {
						tasks = tasksJson.getJSONArray("tasks");
						showVenuesIfClose(tasks, steps);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
			@Override
			public void failure(Object data, String description) {
				// TODO Auto-generated method stub
				
			}
		};
		
		URL url= null;
        
		try {
			url = new URL("http://niyoapi.appspot.com/tasks");
		} catch (MalformedURLException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
			return;
		}
		
		
		Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+url.getPath());
		
		DBJsonFetchTask task = new DBJsonFetchTask(mActivity, caller);
		task.execute(uri);
		
		
	}

	protected void showVenuesIfClose(JSONArray tasks, JSONArray steps) throws JSONException {

		List<AutoVenue> closeVenues = new ArrayList<AutoVenue>();

		for (int i = 0; i < tasks.length(); i++) {
			
			String lat = tasks.getJSONObject(i).getString("lat");
			String lon = tasks.getJSONObject(i).getString("lon");
			
			if (!(lat.equals("null") || lon.equals("null"))){
				String title = tasks.getJSONObject(i).getString("content");
				String id = tasks.getJSONObject(i).getString("id");
				String userInputAddress = tasks.getJSONObject(i).getString("userInputAddress");
				String content = tasks.getJSONObject(i).getString("content");
				
				AutoPoint point = new AutoPoint(Double.parseDouble(lat), Double.parseDouble(lon), title);
				AutoVenue venue = new AutoVenue(title, point, id, userInputAddress, content);

				if (Utils.isVenueClose(venue, steps)){
					closeVenues.add(venue);
				}
			}
			
		}

		if (closeVenues.size() > 0){
			mActivity.showRelevantVenues(closeVenues);
		}

	}

	private void populateFoursquareVenues(String fromLat, String fromLon, String toLat, String toLon,
			JSONArray legs, JSONArray steps) throws JSONException {
		
		String distanceStr = legs.getJSONObject(0).getJSONObject("distance").getString("text");
		Double distance = new Double(distanceStr.replace("km", "").replace(" ", "").replace("m", ""));
		Log.d(LOG_TAG, "distance is "+distance*1000);
		SearchFoursquareVenuesTask task = new SearchFoursquareVenuesTask(mActivity);
		
		String categories = StringUtils.createCommaSeperatedList(mCategoryIds);
		ClientLog.d(LOG_TAG, "categories are "+categories);
		String[] params = new String[7];
		params[0] = Double.toString(distance*1000);
		params[1] = steps.toString();
		params[2] = categories;
		params[3] = fromLat;
		params[4] = fromLon;
		params[5] = toLat;
		params[6] = toLon;
		task.execute(params);
	}

}
