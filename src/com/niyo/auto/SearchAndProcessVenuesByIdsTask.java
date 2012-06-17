package com.niyo.auto;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.niyo.ClientLog;
import com.niyo.StringUtils;
import com.niyo.Utils;
import com.niyo.auto.map.AutoMapActivity;

public class SearchAndProcessVenuesByIdsTask extends AsyncTask<String, Void, List<AutoVenue>> {

	private static final String LOG_TAG = SearchAndProcessVenuesByIdsTask.class.getSimpleName();
	private AutoMapActivity mContext;
	private Map<String, String> mCategoriesToTaskContent;
	private String mFoursquareUrl;
	
	public SearchAndProcessVenuesByIdsTask(AutoMapActivity context, Map<String, String> categoriesToTasks){
		mContext = context;
		mCategoriesToTaskContent = categoriesToTasks;
		ClientLog.d(LOG_TAG, "init with "+StringUtils.printMap((HashMap<String, String>)categoriesToTasks));
	}
	
	@Override
	protected List<AutoVenue> doInBackground(String... params) {
		
		String stepsJsonStr = params[1];
		String categoryIds = "";
		try {
			categoryIds = URLEncoder.encode(params[2].toString(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		List<AutoVenue> listResult = new ArrayList<AutoVenue>();
		
		try {
			
		JSONArray stepsArray = new JSONArray(stepsJsonStr);
		
		ClientLog.d(LOG_TAG, "there are "+stepsArray.length()+" steps");
		
		
		for (int i = 0; i < stepsArray.length(); i++){
			
			JSONObject step = stepsArray.getJSONObject(i);
			JSONObject endLocation = step.getJSONObject("end_location");
			String centerLat = endLocation.getString("$a");
			String centerLon = endLocation.getString("ab");
			ClientLog.d(LOG_TAG, "going with step "+i);
			JSONArray foursquareVenues = getFoursquareVenues(centerLat, centerLon, categoryIds);
			
			for (int j = 0; j < foursquareVenues.length(); j++)
			{
				JSONObject fourVenue = foursquareVenues.getJSONObject(j);
				String venueLat = fourVenue.getJSONObject("location").getString("lat");
				String venueLng = fourVenue.getJSONObject("location").getString("lng");
				String address = "";
				if (fourVenue.getJSONObject("location").has("address"))
				{
					address = fourVenue.getJSONObject("location").getString("address");
				}
				
				String id = fourVenue.getString("id");
				String name = fourVenue.getString("name");
				AutoPoint venuePoint = new AutoPoint(Double.parseDouble(venueLat), Double.parseDouble(venueLng), "");
				AutoVenue autoVenue = new AutoVenue(name, venuePoint, id, address, "from 4sqr");
				
				JSONArray categories = fourVenue.getJSONArray("categories");
				
				for (int k=0; k < categories.length(); k++){
					
					String categoryId = categories.getJSONObject(k).getString("id");
					String taskTitle = mCategoriesToTaskContent.get(categoryId);
					
					if (taskTitle != null){
						autoVenue.setTaskContent(taskTitle);
					}
				}
				
				
				
				if (Utils.isVenueClose(autoVenue, stepsArray)){
//				if (true){
					listResult.add(autoVenue);
				}
			}
		}
		
			
			
			
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return listResult;
	}
	
	private JSONArray getFoursquareVenues(String centerLat, String centerLon,
			String categoryIds) throws JSONException {

		DefaultHttpClient client = new DefaultHttpClient();
		String url = "https://api.foursquare.com/v2/venues/search?ll="+centerLat+","+centerLon +"&categoryId="+categoryIds+"&oauth_token=3MU3QXE3H3KHT33DGG4NMR0KD221DVFMOFQQQ3VOIUQ5DKJY&v=20120404&intent=browse&radius=200&limit=50";
		//		String url = "https://api.foursquare.com/v2/venues/search?ll=31.9599,34.7941&categoryId=4bf58dd8d48988d11b951735%2C4bf58dd8d48988d113951735%2C&oauth_token=3MU3QXE3H3KHT33DGG4NMR0KD221DVFMOFQQQ3VOIUQ5DKJY&v=20120404&intent=browse&radius=33800.0";
		mFoursquareUrl = url;
		ClientLog.d(LOG_TAG, "url to foursquare is "+url);
		HttpGet method = new HttpGet(url);
		String result = null;
		try {
			HttpResponse response = client.execute(method);
			InputStream is = response.getEntity().getContent();
			result = Utils.readString(is);
			ClientLog.d(LOG_TAG, "result is "+result);
			int status = FoursquareUtils.getFoursqaureStatus(result);
			ClientLog.d(LOG_TAG, "status is "+status);
			if (status != HttpStatus.SC_OK){
				ClientLog.e(LOG_TAG, "Error!");
				return null;
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "Error!", e);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "Error!", e);
		}


		JSONObject resultJson = new JSONObject(result);

		JSONObject responseJSon = resultJson.getJSONObject("response");
		JSONArray foursquareVenues = responseJSon.getJSONArray("venues");

		return foursquareVenues;
	}
	
	

	@Override
	protected void onPostExecute(List<AutoVenue> result) 
	{
		Log.d(LOG_TAG, "we have "+result.size()+" close venues:");
//		for (AutoVenue autoVenue : result) {
//			Log.d(LOG_TAG, "name: "+autoVenue.getName()+" The distance is "+autoVenue.getDistance()+" the id "+autoVenue.getFoursqaureId()+" address is "+autoVenue.getAddress());
//		}
		
		if (result.size() > 0){
			mContext.showRelevantVenues(result, mFoursquareUrl);
			
		}
		else{
			mContext.setFoursquareUrl(mFoursquareUrl);
		}
		
		mContext.hideProgressBar();
		
	}
	
	

}
