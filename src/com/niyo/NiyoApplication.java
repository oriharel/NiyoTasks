package com.niyo;

//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.ArrayList;

//import org.json.JSONArray;
//import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.niyo.auto.AutoActivity;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
//import android.content.Intent;
//import android.content.IntentFilter;
import android.util.Log;

//import com.niyo.data.JsonFetchIntentService;

public class NiyoApplication extends Application {

	private static final String LOG_TAG = NiyoApplication.class.getSimpleName();
	private static Boolean s_logEnabled = true;
//	private static Boolean s_logEnabled = false;
//	private JSONArray mFoursqaureVenues;
	GoogleCloudMessaging gcm;
	
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME =
            "onServerExpirationTimeMs";
	public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
	
	public static final String SENDER_ID = "663161706726";
	String mRegid;
	
	public static boolean isLogEnabled() {
		return s_logEnabled;
	}
	
	@Override
	public void onCreate(){
		
		super.onCreate();
		
		mRegid = getRegistrationId();
        
        if (mRegid.length() == 0) {
            registerBackground();
        }
        
//		fetchData();
//		fetchFoursquareVenues();
//		setupProximityAlerts();
	}
	
	private String getRegistrationId() {
		String registrationId = SettingsManager.getString(this, PROPERTY_REG_ID);
		
		if (registrationId == null || registrationId.length() == 0) {
	        Log.v(LOG_TAG, "Registration not found.");
	        return "";
	    }
	    // check if app was updated; if so, it must clear registration id to
	    // avoid a race condition if GCM sends a message
	    int registeredVersion = SettingsManager.getInt(this, PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(this);
	    if (registeredVersion != currentVersion || isRegistrationExpired()) {
	        Log.v(LOG_TAG, "App version changed or registration expired.");
	        return "";
	    }
	    
	    ClientLog.d(LOG_TAG, "registration id found "+registrationId);
	    return registrationId;
	}
	
	private void setRegistrationId(Context context, String regId) {
	    int appVersion = getAppVersion(context);
	    Log.v(LOG_TAG, "Saving regId on app version " + appVersion);
	    SettingsManager.setString(this, PROPERTY_REG_ID, regId);
	    SettingsManager.setInt(this, PROPERTY_APP_VERSION, appVersion);
	    long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;

	    Log.v(LOG_TAG, "Setting registration expiry time to " +
	            new Timestamp(expirationTime));
	    SettingsManager.setLong(this, PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
	}
	
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	private boolean isRegistrationExpired() {
		
	    // checks if the information is not stale
	    long expirationTime =
	            SettingsManager.getLong(this, PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
	    return System.currentTimeMillis() > expirationTime;
	}
	
	private void registerBackground() {
		
		final Application context = this;
		
	    new AsyncTask<Void, Void, String>() {
	    	
	    	@Override
	        protected String doInBackground(Void... params) {
	            String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                mRegid = gcm.register(SENDER_ID);
	                msg = "Device registered, registration id=" + mRegid;

	                // You should send the registration ID to your server over HTTP,
	                // so it can use GCM/HTTP or CCS to send messages to your app.

	                // For this demo: we don't need to send it because the device
	                // will send upstream messages to a server that echo back the message
	                // using the 'from' address in the message.

	                // Save the regid - no need to register again.
	                setRegistrationId(context, mRegid);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	            }
	            return msg;
	        }

	    	@Override
	        protected void onPostExecute(String msg) {
	            ClientLog.d(LOG_TAG, msg + "\n");
	        }
	    }.execute(null, null, null);
	}
	
//	private void setupProximityAlerts() {
//		
//		//for rebuild
//		IntentFilter filter = new IntentFilter(ProximityIntentReciever.TASK_PROXIMITY_ALERT);
//		ProximityIntentReciever reciever = new ProximityIntentReciever();
//		registerReceiver(reciever, filter);
//		Utils.setupProximityAlerts(this);
//		
//	}

//	private void fetchFoursquareVenues() {
//
//		ClientLog.d(LOG_TAG, "reading the categories");
//		try {        
//			BufferedReader in = null;
//			in = new BufferedReader(new InputStreamReader(getAssets().open("categories.json")));
//			String line;
//			StringBuilder sb = new StringBuilder();
//			
//			while((line =in.readLine()) != null){
//				sb.append(line);
//			}
//			
//			in.close();
//			//create a json object
//			JSONObject json = new JSONObject(sb.toString());
//
//			//loop over the items array and look for pid
//			JSONArray categories = json.getJSONObject("response").getJSONArray("categories");
//			setFoursqaureVenues(categories);
//			
//			//TODO - to remove. expensinve loop
//			for (int i = 0; i < getFoursqaureVenues().length(); i++){
//				ClientLog.d(LOG_TAG, "category: "+getFoursqaureVenues().getJSONObject(i).getString("name"));
//			}
//			
//		} catch (Exception e) {
//			ClientLog.e(LOG_TAG, "Error!", e);
//		}
//
//
//	}

//	public void fetchData(){
//		ArrayList<String> urls = new ArrayList<String>();
//		try {
//			urls.add("http://niyoapi.appspot.com/categories");
//			urls.add("http://niyoapi.appspot.com/tasks");
//			urls.add("http://niyoapi.appspot.com/getFlatTasks");
//			urls.add("https://api.foursquare.com/v2/venues/categories?oauth_token=3MU3QXE3H3KHT33DGG4NMR0KD221DVFMOFQQQ3VOIUQ5DKJY&v=20120424");
//			
//			Intent intent = new Intent(this, JsonFetchIntentService.class);
//			intent.putStringArrayListExtra(JsonFetchIntentService.URLS_EXTRA, urls);
//			startService(intent);
//			
//		} catch (Exception e) {
//			ClientLog.e(LOG_TAG, "Error!", e);
//		}
//	}

//	public JSONArray getFoursqaureVenues() {
//		return mFoursqaureVenues;
//	}

//	public void setFoursqaureVenues(JSONArray foursqaureVenues) {
//		mFoursqaureVenues = foursqaureVenues;
//	}

}
