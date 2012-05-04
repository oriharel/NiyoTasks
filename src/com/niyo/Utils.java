package com.niyo;

import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.niyo.auto.AutoPoint;
import com.niyo.auto.AutoVenue;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utils {

	public static Comparator<JSONObject> getTaskJSONComparator() {

		Comparator<JSONObject> result = new Comparator<JSONObject>() {

			@Override
			public int compare(JSONObject lTask, JSONObject rTask) {

				String lContent = "";
				String rContent = "";
				try {
					
					lContent = lTask.getString("content");
					rContent = rTask.getString("content");
					
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return lContent.compareTo(rContent);
			}
		};

		return result;
	}
	
	public static boolean isVenueClose(AutoVenue autoVenue, JSONArray stepsArray) {
		
//		Log.d(LOG_TAG, "stepsArrya is "+stepsArray.length());
		boolean result = false;
		AutoPoint venuePoint = autoVenue.getLocation();
		for (int i = 0; i< stepsArray.length(); i++)
		{
			try {
				String stepLat = stepsArray.getJSONObject(i).getJSONObject("end_location").getString("$a");
				String stepLon = stepsArray.getJSONObject(i).getJSONObject("end_location").getString("ab");
				
				AutoPoint fromPoint = new AutoPoint(Double.parseDouble(stepLat), Double.parseDouble(stepLon));
				
				Double distance = getDistance(fromPoint, venuePoint);
				
//				Log.d(LOG_TAG, "distance is "+distance);
				
				if (distance < 500)
				{
					autoVenue.setDistance(distance);
					return true;
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private static Double getDistance(AutoPoint from, AutoPoint to)
	{
		Double R = 6371.0; // km
		Double dLat = Math.toRadians((to.getLat()-from.getLat()));
		Double dLon = Math.toRadians((to.getLon()-from.getLon()));
		Double lat1 = Math.toRadians(from.getLat());
		Double lat2 = Math.toRadians(to.getLat());

		Double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		        Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		Double d = R * c;
//		Log.d(LOG_TAG, "distance is "+d);
		return d*1000;//convert to meters
	}
	
	public static void hideKeyboard(Context context, View view) 
	{
		IBinder binder = view.getWindowToken();
		InputMethodManager service = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		service.hideSoftInputFromWindow(binder, 0);
	}
	
	public static void showKeyboard(Context context, View view) {
		IBinder binder = view.getWindowToken();
		InputMethodManager service = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		service.showSoftInputFromInputMethod(binder, InputMethodManager.SHOW_FORCED);
	}
	
	public static void showKeyboard2(Context context) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY); 
	}

}
