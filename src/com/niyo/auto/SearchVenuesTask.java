package com.niyo.auto;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class SearchVenuesTask extends AsyncTask<String, Void, List<AutoVenue>> {

	private static final String LOG_TAG = SearchVenuesTask.class.getSimpleName();
	@Override
	protected List<AutoVenue> doInBackground(String... params) {
		Double distance = new Double(params[0]);
		String stepsJsonStr = params[1];
		String categoryId = params[2];
		DefaultHttpClient client = new DefaultHttpClient();
		String url = "https://api.foursquare.com/v2/venues/search?ll=32.179402,34.858187" +
				"&categoryId="+categoryId+"&oauth_token=3MU3QXE3H3KHT33DGG4NMR0KD221DVFMOFQQQ3VOIUQ5DKJY&v=20120404&intent=browse&radius="+distance;
		HttpGet method = new HttpGet(url);
		String result = null;
		List<AutoVenue> listResult = new ArrayList<AutoVenue>();
		try {
			HttpResponse response = client.execute(method);
			InputStream is = response.getEntity().getContent();
			result = readString(is);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "Error!", e);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "Error!", e);
		}
		
		try {
			JSONObject resultJson = new JSONObject(result);
			
			JSONObject responseJSon = resultJson.getJSONObject("response");
			JSONArray venues = responseJSon.getJSONArray("venues");
			Log.d(LOG_TAG, "stepsJsonStr is "+stepsJsonStr);
			JSONArray stepsArray = new JSONArray(stepsJsonStr);
			
			for (int i = 0; i < venues.length(); i++)
			{
				String venueLat = venues.getJSONObject(i).getJSONObject("location").getString("lat");
				String venueLng = venues.getJSONObject(i).getJSONObject("location").getString("lng");
				String address = "";
				if (venues.getJSONObject(i).getJSONObject("location").has("address"))
				{
					address = venues.getJSONObject(i).getJSONObject("location").getString("address");
				}
				
				String id = venues.getJSONObject(i).getString("id");
				
				AutoPoint venuePoint = new AutoPoint(Double.parseDouble(venueLat), Double.parseDouble(venueLng));
				AutoVenue autoVenue = new AutoVenue(venues.getJSONObject(i).getString("name"), venuePoint, id, address);
				
				if (isVenueClose(autoVenue, stepsArray))
				{
					listResult.add(autoVenue);
				}
				
//				AutoVenue[] asArray = (AutoVenue[])listResult.toArray();
//				Arrays.sort(asArray);
//				for (AutoVenue autoVenue : asArray) {
//					listResult.add(autoVenue);
//				}
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return listResult;
	}
	
	private boolean isVenueClose(AutoVenue autoVenue, JSONArray stepsArray) {
		
//		Log.d(LOG_TAG, "stepsArrya is "+stepsArray.length());
		boolean result = false;
		AutoPoint venuePoint = autoVenue.getLocation();
		for (int i = 0; i< stepsArray.length(); i++)
		{
			try {
				String stepLat = stepsArray.getJSONObject(i).getJSONObject("end_location").getString("Xa");
				String stepLon = stepsArray.getJSONObject(i).getJSONObject("end_location").getString("Ya");
				
				AutoPoint fromPoint = new AutoPoint(Double.parseDouble(stepLat), Double.parseDouble(stepLon));
				
				Double distance = getDistance(fromPoint, venuePoint);
				
//				Log.d(LOG_TAG, "distance is "+distance);
				
				if (distance < 100)
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

	private static String readString(InputStream inputStream) 
	{
		int BUFFER_SIZE = 20000;
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		ByteArrayBuffer baf = new ByteArrayBuffer(BUFFER_SIZE);
		byte[] buffer = new byte[BUFFER_SIZE];
		int current = 0;

		try {
			while ((current = bis.read(buffer)) != -1) 
			{
				baf.append(buffer, 0, current);
			}
		} catch (IOException e) 
		{
			Log.e(LOG_TAG, "Error", e);
		}

		return new String(baf.toByteArray());
	}
	
	@Override
	protected void onPostExecute(List<AutoVenue> result) 
	{
		Log.d(LOG_TAG, "we have "+result.size()+" close venues:");
		for (AutoVenue autoVenue : result) {
			Log.d(LOG_TAG, "name: "+autoVenue.getName()+" The distance is "+autoVenue.getDistance()+" the id "+autoVenue.getFoursqaureId()+" address is "+autoVenue.getAddress());
		}
	}
	
	private Double getDistance(AutoPoint from, AutoPoint to)
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

}
