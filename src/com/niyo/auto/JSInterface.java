package com.niyo.auto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.niyo.auto.AutoConstants;
import com.niyo.auto.SearchVenuesTask;

import android.util.Log;

public class JSInterface {
	
	private static final String LOG_TAG = JSInterface.class.getSimpleName();
	
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
			
			String distanceStr = legs.getJSONObject(0).getJSONObject("distance").getString("text");
			Double distance = new Double(distanceStr.replace("km", "").replace(" ", "").replace("m", ""));
			Log.d(LOG_TAG, "distance is "+distance*1000);
			SearchVenuesTask task = new SearchVenuesTask();
			String[] params = new String[7];
			params[0] = Double.toString(distance*1000);
			params[1] = steps.toString();
			params[2] = AutoConstants.GROCERY_OR_SUPERMARKET;
			params[3] = fromLat;
			params[4] = fromLon;
			params[5] = toLat;
			params[6] = toLon;
	        task.execute(params);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "Error!", e);
		}
	}

}
