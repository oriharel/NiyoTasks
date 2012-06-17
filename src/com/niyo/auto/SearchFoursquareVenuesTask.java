//package com.niyo.auto;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.ByteArrayBuffer;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.niyo.ClientLog;
//import com.niyo.Utils;
//import com.niyo.auto.map.AutoMapActivity;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//public class SearchFoursquareVenuesTask extends AsyncTask<String, Void, List<AutoVenue>> {
//
//	private static final String LOG_TAG = SearchFoursquareVenuesTask.class.getSimpleName();
//	private AutoMapActivity mContext;
//	
//	public SearchFoursquareVenuesTask(AutoMapActivity context){
//		mContext = context;
//	}
//	
//	@Override
//	protected List<AutoVenue> doInBackground(String... params) {
//		
//		Double distance = new Double(params[0]);
//		String stepsJsonStr = params[1];
//		String categoryIds = "";
//		try {
//			categoryIds = URLEncoder.encode(params[2].toString(), "UTF-8");
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
//		
//		DefaultHttpClient client = new DefaultHttpClient();
//		String url = "https://api.foursquare.com/v2/venues/search?ll="+params[3]+","+params[4] +
//				"&categoryId="+categoryIds+"&oauth_token=3MU3QXE3H3KHT33DGG4NMR0KD221DVFMOFQQQ3VOIUQ5DKJY&v=20120404&intent=browse&radius="+distance;
//		ClientLog.d(LOG_TAG, "url to foursquare is "+url);
//		HttpGet method = new HttpGet(url);
//		String result = null;
//		List<AutoVenue> listResult = new ArrayList<AutoVenue>();
//		try {
//			HttpResponse response = client.execute(method);
//			InputStream is = response.getEntity().getContent();
//			result = readString(is);
////			ClientLog.d(LOG_TAG, "result is "+result);
//			int status = getFoursqaureStatus(result);
//			ClientLog.d(LOG_TAG, "status is "+status);
//			if (status != HttpStatus.SC_OK){
////				ClientLog.e(LOG_TAG, "Error!");
//				return new ArrayList<AutoVenue>();
//			}
//			
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//			Log.e(LOG_TAG, "Error!", e);
//		} catch (IOException e) {
//			e.printStackTrace();
//			Log.e(LOG_TAG, "Error!", e);
//		}
//		
//		try {
//			JSONObject resultJson = new JSONObject(result);
//			
//			JSONObject responseJSon = resultJson.getJSONObject("response");
//			JSONArray foursquareVenues = responseJSon.getJSONArray("venues");
//			JSONArray stepsArray = new JSONArray(stepsJsonStr);
//			
//			for (int i = 0; i < foursquareVenues.length(); i++)
//			{
//				String venueLat = foursquareVenues.getJSONObject(i).getJSONObject("location").getString("lat");
//				String venueLng = foursquareVenues.getJSONObject(i).getJSONObject("location").getString("lng");
//				String address = "";
//				if (foursquareVenues.getJSONObject(i).getJSONObject("location").has("address"))
//				{
//					address = foursquareVenues.getJSONObject(i).getJSONObject("location").getString("address");
//				}
//				
//				String id = foursquareVenues.getJSONObject(i).getString("id");
//				String name = foursquareVenues.getJSONObject(i).getString("name");
////				ClientLog.d(LOG_TAG, "venue name is "+name);
//				AutoPoint venuePoint = new AutoPoint(Double.parseDouble(venueLat), Double.parseDouble(venueLng), "");
//				AutoVenue autoVenue = new AutoVenue(name, venuePoint, id, address, "from 4sqr");
//				
//				if (Utils.isVenueClose(autoVenue, stepsArray)){
////					ClientLog.d(LOG_TAG, autoVenue.getName()+" is close enough");
//					listResult.add(autoVenue);
//				}
//				else{
////					ClientLog.d(LOG_TAG, autoVenue.getName()+" is not close enough");
//				}
//				
////				AutoVenue[] asArray = (AutoVenue[])listResult.toArray();
////				Arrays.sort(asArray);
////				for (AutoVenue autoVenue : asArray) {
////					listResult.add(autoVenue);
////				}
//			}
//			
//			
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return listResult;
//	}
//	
//	private int getFoursqaureStatus(String response) {
//		try {
//			JSONObject json;
//
//			json = new JSONObject(response);
//
//
//			if (json.has("meta")){
//				String code = json.getJSONObject("meta").getString("code");
//				return new Integer(code);
//			}
//		} catch (JSONException e) {
//			ClientLog.e(LOG_TAG, "Error!", e);
//		}
//
//		ClientLog.e(LOG_TAG, "couldn't read meta");
//		return -1;
//	}
//
//	
//
//	private static String readString(InputStream inputStream) 
//	{
//		int BUFFER_SIZE = 20000;
//		BufferedInputStream bis = new BufferedInputStream(inputStream);
//		ByteArrayBuffer baf = new ByteArrayBuffer(BUFFER_SIZE);
//		byte[] buffer = new byte[BUFFER_SIZE];
//		int current = 0;
//
//		try {
//			while ((current = bis.read(buffer)) != -1) 
//			{
//				baf.append(buffer, 0, current);
//			}
//		} catch (IOException e) 
//		{
//			Log.e(LOG_TAG, "Error", e);
//		}
//
//		return new String(baf.toByteArray());
//	}
//	
//	@Override
//	protected void onPostExecute(List<AutoVenue> result) 
//	{
//		Log.d(LOG_TAG, "we have "+result.size()+" close venues:");
////		for (AutoVenue autoVenue : result) {
////			Log.d(LOG_TAG, "name: "+autoVenue.getName()+" The distance is "+autoVenue.getDistance()+" the id "+autoVenue.getFoursqaureId()+" address is "+autoVenue.getAddress());
////		}
//		
//		if (result.size() > 0){
//			mContext.showRelevantVenues(result);
//		}
//		
//	}
//	
//	
//
//}
