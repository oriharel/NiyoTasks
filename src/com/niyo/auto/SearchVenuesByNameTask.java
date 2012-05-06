package com.niyo.auto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.niyo.ClientLog;
import com.niyo.LocationUtil;
import com.niyo.ServiceCaller;
import com.niyo.Utils;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class SearchVenuesByNameTask extends AsyncTask<String, Void, List<AutoVenue>> {

	private static final String LOG_TAG = SearchVenuesByNameTask.class.getSimpleName();
	private ServiceCaller mCaller;
	private Context mContext;
	
	public SearchVenuesByNameTask(Context context, ServiceCaller caller){
		
		mCaller = caller;
		mContext = context;
	}
	
	@Override
	protected List<AutoVenue> doInBackground(String... params) {
		
		String query = params[0];
		String locationStr = params[1];
		
		DefaultHttpClient client = new DefaultHttpClient();
		String url = "https://api.foursquare.com/v2/venues/search?ll="+locationStr+"&query="+query+"&oauth_token=3MU3QXE3H3KHT33DGG4NMR0KD221DVFMOFQQQ3VOIUQ5DKJY&v=20120506";
		ClientLog.d(LOG_TAG, "url to foursquare is "+url);
		HttpGet method = new HttpGet(url);
		String result = null;
		List<AutoVenue> listResult = new ArrayList<AutoVenue>();
		
		try {
			HttpResponse response = client.execute(method);
			InputStream is = response.getEntity().getContent();
			result = Utils.readString(is);
			int status = FoursquareUtils.getFoursqaureStatus(result);
			
			ClientLog.d(LOG_TAG, "status is "+status);
			
			
			if (status != HttpStatus.SC_OK){
				return new ArrayList<AutoVenue>();
			}
			
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
			JSONArray foursquareVenues = responseJSon.getJSONArray("venues");
			
			for (int i = 0; i < foursquareVenues.length(); i++)
			{
				String venueLat = foursquareVenues.getJSONObject(i).getJSONObject("location").getString("lat");
				String venueLng = foursquareVenues.getJSONObject(i).getJSONObject("location").getString("lng");
				String address = "";
				if (foursquareVenues.getJSONObject(i).getJSONObject("location").has("address"))
				{
					address = foursquareVenues.getJSONObject(i).getJSONObject("location").getString("address");
				}
				
				String id = foursquareVenues.getJSONObject(i).getString("id");
				String name = foursquareVenues.getJSONObject(i).getString("name");
				AutoPoint venuePoint = new AutoPoint(Double.parseDouble(venueLat), Double.parseDouble(venueLng), "");
				AutoVenue autoVenue = new AutoVenue(name, venuePoint, id, address, "from 4sqr");
				
				Location location = LocationUtil.getLocation(mContext);
				Double ditance = Utils.getDistance(location.getLatitude(), location.getLongitude(), venuePoint.getLat(), venuePoint.getLon());
				autoVenue.setDistance(ditance);
				listResult.add(autoVenue);
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return listResult;
	}
	
	@Override
    protected void onPostExecute(List<AutoVenue> result) 
	{
        if (isCancelled()) 
        {
        	ClientLog.d(LOG_TAG, "isCancelled activated");
        }
        
        ClientLog.d(LOG_TAG, "calling success");
        try 
        {
        	if (mCaller != null){
        		mCaller.success(result);
        	}
			
		} 
        catch (Exception e) 
        {
			ClientLog.e(LOG_TAG, "Error! with ", e);
		}
    }

}
