package com.niyo.auto;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.niyo.ClientLog;
import com.niyo.LocationUtil;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.SettingsActivity;
import com.niyo.SettingsManager;
import com.niyo.StringUtils;
import com.niyo.auto.map.ChooseAppDialog;
import com.niyo.auto.map.MyLocationListener;
import com.niyo.data.NiyoContentProvider;
import com.niyo.radar.RadarActivity;

public class AutoActivity extends NiyoAbstractActivity {
    /** Called when the activity is first created. */
	
	private static final String LOG_TAG = AutoActivity.class.getSimpleName();
	
	private static final String BOX_TITLE_1_KEY = "boxTitle1";
	private static final String BOX_TITLE_2_KEY = "boxTitle2";
	private static final String BOX_TITLE_3_KEY = "boxTitle3";
	private static final String BOX_TITLE_4_KEY = "boxTitle4";
	private static final String BOX_TITLE_5_KEY = "boxTitle5";
	private static final String BOX_TITLE_6_KEY = "boxTitle6";
	
	private static final String BOX_LAT_1_KEY = "boxLat1";
	private static final String BOX_LAT_2_KEY = "boxLat2";
	private static final String BOX_LAT_3_KEY = "boxLat3";
	private static final String BOX_LAT_4_KEY = "boxLat4";
	private static final String BOX_LAT_5_KEY = "boxLat5";
	private static final String BOX_LAT_6_KEY = "boxLat6";
	
	private static final String BOX_LON_1_KEY = "boxLon1";
	private static final String BOX_LON_2_KEY = "boxLon2";
	private static final String BOX_LON_3_KEY = "boxLon3";
	private static final String BOX_LON_4_KEY = "boxLon4";
	private static final String BOX_LON_5_KEY = "boxLon5";
	private static final String BOX_LON_6_KEY = "boxLon6";
	
//	public static final String PROPERTY_REG_ID = "registration_id";
//	private static final String PROPERTY_APP_VERSION = "appVersion";
//	private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME =
//            "onServerExpirationTimeMs";
	
	/**
     * Default lifespan (7 days) of a reservation until it is considered expired.
     */
//    public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
//	
//	public static final String SENDER_ID = "663161706726";

	private static final int GO_TO_TODO_LIST_CONTEXT_MENU_ITEM = 0;

	private static final int RESTORE_DEFAULTS_CONTEXT_MENU_ITEM = 1;

	public static final String SIX_PACK_CHANGED = "sixPackChangedKey";

	public static final String MAP_PREFERENCE_KEY = "mapsPrefKey";

	private static final String GOOGLE_MAPS = "googleMaps";

	private static final int SETTINGS = 4;
	private static final int RADAR = 5;
	
	private Uri mUri;
	private JSONArray mTasks;
	private Boolean mTasksReady = false;
	
//	private Map<Integer, String> mDefaultsTitles;
//	private Map<Integer, String> mDefaultsLocations;
	
//	private MyLocationListener mGpsListener;
//	private MyLocationListener mNetworkListener;
	private MyLocationListener mPassiveListener;
	
//	String mRegid;
	GoogleCloudMessaging gcm;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.where_to_layout);
        PreferenceManager.setDefaultValues(this, R.xml.niyo_prefs, false);
        Log.d(LOG_TAG, "onCreate started");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setupBoxBtnsListeners();
        
//        mRegid = getRegistrationId();
//        
//        if (mRegid.length() == 0) {
//            registerBackground();
//        }
        
        
        URL url= null;
        
		try {
			url = new URL("http://niyoapi.appspot.com/tasks");
		} catch (MalformedURLException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
			return;
		}
		
		
		Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+url.getPath());
		setUri(uri);
//        loadTasksFromDb();
        
		gcm = GoogleCloudMessaging.getInstance(this);
        
        setupOnClicksForOldSDK();
        
        initLocationListeners();
        
    }

//	private String getRegistrationId() {
//		String registrationId = SettingsManager.getString(this, PROPERTY_REG_ID);
//		
//		if (registrationId == null || registrationId.length() == 0) {
//	        Log.v(LOG_TAG, "Registration not found.");
//	        return "";
//	    }
//	    // check if app was updated; if so, it must clear registration id to
//	    // avoid a race condition if GCM sends a message
//	    int registeredVersion = SettingsManager.getInt(this, PROPERTY_APP_VERSION, Integer.MIN_VALUE);
//	    int currentVersion = getAppVersion(this);
//	    if (registeredVersion != currentVersion || isRegistrationExpired()) {
//	        Log.v(LOG_TAG, "App version changed or registration expired.");
//	        return "";
//	    }
//	    
//	    ClientLog.d(LOG_TAG, "registration id found "+registrationId);
//	    return registrationId;
//	}
	
//	private boolean isRegistrationExpired() {
//		
//	    // checks if the information is not stale
//	    long expirationTime =
//	            SettingsManager.getLong(this, PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
//	    return System.currentTimeMillis() > expirationTime;
//	}
	
//	private static int getAppVersion(Context context) {
//	    try {
//	        PackageInfo packageInfo = context.getPackageManager()
//	                .getPackageInfo(context.getPackageName(), 0);
//	        return packageInfo.versionCode;
//	    } catch (NameNotFoundException e) {
//	        // should never happen
//	        throw new RuntimeException("Could not get package name: " + e);
//	    }
//	}
	
//	private void registerBackground() {
//		
//		final AutoActivity context = this;
//		
//	    new AsyncTask<Void, Void, String>() {
//	    	
//	    	@Override
//	        protected String doInBackground(Void... params) {
//	            String msg = "";
//	            try {
//	                if (gcm == null) {
//	                    gcm = GoogleCloudMessaging.getInstance(context);
//	                }
//	                mRegid = gcm.register(SENDER_ID);
//	                msg = "Device registered, registration id=" + mRegid;
//
//	                // You should send the registration ID to your server over HTTP,
//	                // so it can use GCM/HTTP or CCS to send messages to your app.
//
//	                // For this demo: we don't need to send it because the device
//	                // will send upstream messages to a server that echo back the message
//	                // using the 'from' address in the message.
//
//	                // Save the regid - no need to register again.
//	                setRegistrationId(context, mRegid);
//	            } catch (IOException ex) {
//	                msg = "Error :" + ex.getMessage();
//	            }
//	            return msg;
//	        }
//
//	    	@Override
//	        protected void onPostExecute(String msg) {
//	            ClientLog.d(LOG_TAG, msg + "\n");
//	        }
//	    }.execute(null, null, null);
//	}
	
	/**
	 * Stores the registration id, app versionCode, and expiration time in the
	 * application's {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration id
	 */
//	private void setRegistrationId(Context context, String regId) {
//	    int appVersion = getAppVersion(context);
//	    Log.v(LOG_TAG, "Saving regId on app version " + appVersion);
//	    SettingsManager.setString(this, PROPERTY_REG_ID, regId);
//	    SettingsManager.setInt(this, PROPERTY_APP_VERSION, appVersion);
//	    long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;
//
//	    Log.v(LOG_TAG, "Setting registration expiry time to " +
//	            new Timestamp(expirationTime));
//	    SettingsManager.setLong(this, PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
//	}


	private void initLocationListeners() {
    	
//    	String gps = LocationManager.GPS_PROVIDER;
//		String network = LocationManager.NETWORK_PROVIDER;
//		String passive = LocationManager.PASSIVE_PROVIDER;
		
//    	mGpsListener = new MyLocationListener(this, gps);
//    	mNetworkListener = new MyLocationListener(this, network);
//    	mPassiveListener = new MyLocationListener(this, passive);
	}


	private void setupOnClicksForOldSDK() {
    	
    	final int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
    	ClientLog.d(LOG_TAG, "sdk version is: "+sdkVersion);
    	if (sdkVersion <= Build.VERSION_CODES.CUPCAKE) {
    		
    		findViewById(R.id.boxBtn1).setOnClickListener(getNavigateToListener());
    		findViewById(R.id.boxBtn2).setOnClickListener(getNavigateToListener());
    		findViewById(R.id.boxBtn3).setOnClickListener(getNavigateToListener());
    		findViewById(R.id.boxBtn4).setOnClickListener(getNavigateToListener());
    		findViewById(R.id.boxBtn5).setOnClickListener(getNavigateToListener());
    		findViewById(R.id.boxBtn6).setOnClickListener(getNavigateToListener());
    	}
	}


	private OnClickListener getNavigateToListener() {
		
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				navigateTo(v);
			}
		};
	}


	private void getNextCalendarEvent() 
    {
		ServiceCaller caller = new ServiceCaller() {
			
			@Override
			public void success(Object data) {
				
				if (data != null){
					AutoEvent event = (AutoEvent)data;
					ClientLog.d(LOG_TAG, "next event is: "+event.getTitle());
					
					setupSixBox(event);
				}
				else{
					ClientLog.d(LOG_TAG, "couldn't find any next event");
				}
				
				
				
			}
			
			@Override
			public void failure(Object data, String description) {
				ClientLog.e(LOG_TAG, "Error! "+description);
				
			}
		};
		
		VersionedTaskInvoker invoker = VersionedTaskInvoker.newInstance(this, caller);
		invoker.invokeTask();
	}


	protected void setupSixBox(AutoEvent event) {
		
		ClientLog.d(LOG_TAG, "event getLat "+event.getLat());
		if(!TextUtils.isEmpty(event.getLat()) && !TextUtils.isEmpty(event.getLon())){
			TextView btn6Name = (TextView)findViewById(R.id.box5Name);
			btn6Name.setText(event.getTitle());
			View btn5 = findViewById(R.id.boxBtn5);
			btn5.setTag(event.getLat()+","+event.getLon());
			findViewById(R.id.addNewCross5).setVisibility(View.GONE);
			findViewById(R.id.box5Name).setVisibility(View.VISIBLE);
		}
		
		
	}


	private void initializeBoxes() {
		
		for (int i = 1; i < 6; i++) {
			String currBoxTitle = "boxTitle"+i;
			String currBoxLat = "boxLat"+i;
			String currBoxLon = "boxLon"+i;
			
			ClientLog.d(LOG_TAG, "currBoxTitle="+currBoxTitle+" currBoxLat="+currBoxLat+" currBoxLon="+currBoxLon);
			
			Float storedLat = SettingsManager.getFloat(this, currBoxLat, 0);
			Float storedLon = SettingsManager.getFloat(this, currBoxLon, 0);
			String storedTitle = SettingsManager.getString(this, currBoxTitle);
			ClientLog.d(LOG_TAG, "storedLat="+storedLat+" storedLon="+storedLon+" storedTitle="+storedTitle);
			
			int crossId = getResources().getIdentifier("id/addNewCross"+i, null, getPackageName());
			int nameId = getResources().getIdentifier("id/box"+i+"Name", null, getPackageName());
			int boxId = getResources().getIdentifier("id/boxBtn"+i, null, getPackageName());
			ClientLog.d(LOG_TAG, "crossId="+crossId+" nameId="+nameId+" boxId="+boxId);
			View boxView = findViewById(boxId);
			
			if (storedLat != 0 && storedLon != 0) {
				
				findViewById(crossId).setVisibility(View.GONE);
				TextView boxName = (TextView)findViewById(nameId);
				boxName.setVisibility(View.VISIBLE);
				boxView.setTag(storedLat+","+storedLon);
				boxName.setText(storedTitle);
				
			}
			else {
				
				findViewById(crossId).setVisibility(View.VISIBLE);
				findViewById(nameId).setVisibility(View.GONE);
			}
		}
    	
    	
	}


	@Override
    protected void onResume(){
    	super.onResume();
//    	setupBoxes();
    	initializeBoxes();
    	getNextCalendarEvent();
    	ClientLog.d(LOG_TAG, "going to update location");
    	LocationUtil.updateLocation(this, null, null, mPassiveListener);
    }
	
	@Override
	protected void onPause(){
		super.onPause();
		LocationUtil.removeLocationUpdates(this, null, null, mPassiveListener);
	}
	
	
	private void setupBoxes() {
    	
//		if (hasUSerChanged()){
//			List<Integer> boxIds = new ArrayList<Integer>();
//	    	
//	    	boxIds.add(R.id.boxBtn1);
//	    	boxIds.add(R.id.boxBtn2);
//	    	boxIds.add(R.id.boxBtn3);
//	    	boxIds.add(R.id.boxBtn4);
//	    	boxIds.add(R.id.boxBtn5);
//	    	boxIds.add(R.id.boxBtn6);
//	    	
//	    	for (Integer boxId : boxIds) {
//				
//	    		Button btn = (Button)findViewById(boxId);
//	    		
//	    		String title = SettingsManager.getString(this, getTitleKey(btn));
//	    		
//	    		if (title != null && !title.equals("")){
//	    			btn.setText(title);
//	    		}
//	    		
//	    		Float lat = SettingsManager.getFloat(this, getLatKey(btn), 0);
//	    		Float lon = SettingsManager.getFloat(this, getLonKey(btn), 0);
//	    		ClientLog.d(LOG_TAG, "for "+boxId+" I got lat "+lat+" and lon "+lon);
//	    		if (! (lat == 0 || lon == 0)){
//	    			String tag = lat+","+lon;
//	    			btn.setTag(tag);
//	    		}
//			}
//		}
    	
		
	}
	
	private boolean hasUSerChanged() {
		return SettingsManager.getBoolean(this, SIX_PACK_CHANGED, false);
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) 
	{
		ClientLog.d(LOG_TAG, "onCreateOptionsMenu started");
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				MenuItem settingsMenuItem1 = menu.add(0, SETTINGS, 0, "Settings");
				settingsMenuItem1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
				settingsMenuItem1.setIcon(R.drawable.ic_menu_settings_holo_light);
				
//				MenuItem settingsMenuItem2 = menu.add(0, RADAR, 0, "Radar");
//				settingsMenuItem2.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//				settingsMenuItem2.setIcon(R.drawable.ic_radar);
		    }
			
		} catch (Exception e) 
		{
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem menuItem) 
	{
    	if (menuItem.getItemId() == GO_TO_TODO_LIST_CONTEXT_MENU_ITEM){
    		
//    		Intent intent = CategoryTasksActivity.getCreationIntent(this);
//        	startActivity(intent);
        	return true;
    	}
    	else if (menuItem.getItemId() == RESTORE_DEFAULTS_CONTEXT_MENU_ITEM){
    		
    		resotreDefaults();
        	return true;
    	}
    	else if (menuItem.getItemId() == RADAR){
    		
    		Intent intent = RadarActivity.getCreationIntent(this);
    		startActivity(intent);
        	return true;
    	}
		else if (menuItem.getItemId() == SETTINGS){
    		
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				Intent intent = SettingsActivity.getCreationIntent(this);
	    		startActivity(intent);
	        	return true;
		    }
			else {
				return false;
			}
    		
    	}
    	else{
    		return false;
    	}
    	
	}

	private void resotreDefaults() {
		
	}


	private void setupBoxBtnsListeners() {
		
    	findViewById(R.id.boxBtn1).setOnLongClickListener(getBoxLongClickListener());
    	findViewById(R.id.boxBtn2).setOnLongClickListener(getBoxLongClickListener());
    	findViewById(R.id.boxBtn3).setOnLongClickListener(getBoxLongClickListener());
    	findViewById(R.id.boxBtn4).setOnLongClickListener(getBoxLongClickListener());
    	findViewById(R.id.boxBtn5).setOnLongClickListener(getBoxLongClickListener());
    	findViewById(R.id.boxBtn6).setOnLongClickListener(getBoxLongClickListener());
	}

	private OnLongClickListener getBoxLongClickListener() {
		
		
		OnLongClickListener result = new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				ClientLog.d(LOG_TAG, "locations of button is "+v.getTag());
				startBoxConfigActivity(v);
				
				return true;
				
			}
		};
		return result;
	}
	
	private void startBoxConfigActivity(View v) {
		
		int playAvailabilityCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (playAvailabilityCode == ConnectionResult.SUCCESS) {
//			Button btn = (Button)v;
//			String currentName = btn.getText().toString();
			Intent intent = CreateAutoBoxAcitivty.getCreationIntent(AutoActivity.this,
					getTitleKey(v), getLatKey(v), getLonKey(v), "");
			startActivity(intent);
			
		}
		else {
			Toast.makeText(this, "No Google Play Services", Toast.LENGTH_LONG).show();
			GooglePlayServicesUtil.getErrorDialog(playAvailabilityCode, this, 0);
		}
	}

	protected String getLonKey(View v) {

		int boxId = v.getId();

		switch (boxId) {
		case R.id.boxBtn1:
			return BOX_LON_1_KEY;
		case R.id.boxBtn2:
			return BOX_LON_2_KEY;
		case R.id.boxBtn3:
			return BOX_LON_3_KEY;
		case R.id.boxBtn4:
			return BOX_LON_4_KEY;
		case R.id.boxBtn5:
			return BOX_LON_5_KEY;
		case R.id.boxBtn6:
			return BOX_LON_6_KEY;

		default:
			break;
		}

		return null;
	}

	protected String getLatKey(View v) {
		
		int boxId = v.getId();

		switch (boxId) {
		case R.id.boxBtn1:
			return BOX_LAT_1_KEY;
		case R.id.boxBtn2:
			return BOX_LAT_2_KEY;
		case R.id.boxBtn3:
			return BOX_LAT_3_KEY;
		case R.id.boxBtn4:
			return BOX_LAT_4_KEY;
		case R.id.boxBtn5:
			return BOX_LAT_5_KEY;
		case R.id.boxBtn6:
			return BOX_LAT_6_KEY;

		default:
			break;
		}

		return null;
	}

	protected String getTitleKey(View v) 
	{
		int boxId = v.getId();
		
		switch (boxId) {
		case R.id.boxBtn1:
			return BOX_TITLE_1_KEY;
		case R.id.boxBtn2:
			return BOX_TITLE_2_KEY;
		case R.id.boxBtn3:
			return BOX_TITLE_3_KEY;
		case R.id.boxBtn4:
			return BOX_TITLE_4_KEY;
		case R.id.boxBtn5:
			return BOX_TITLE_5_KEY;
		case R.id.boxBtn6:
			return BOX_TITLE_6_KEY;

		default:
			break;
		}
		
		return null;
	}

//	private void loadTasksFromDb() {
//		
//		ServiceCaller caller = new ServiceCaller() {
//			
//			@Override
//			public void success(Object data) {
//				
//				if (data != null){
//					JSONObject categoriesData = (JSONObject)data;
//					try {
//						setTasks(categoriesData.getJSONArray("tasks"));
//					} catch (JSONException e) {
//						ClientLog.e(LOG_TAG, "Error!", e);
//					}
//					
//				}
//				else{
//					ClientLog.d(LOG_TAG, "categories list is empty in activity");
//					setTasks(new JSONArray());
//				}
//				
//			}
//			
//			@Override
//			public void failure(Object data, String description) {
//				ClientLog.e(LOG_TAG, "Error in service caller");
//				
//			}
//		};
//		
//		DBJsonFetchTask task = new DBJsonFetchTask(this, caller);
//		task.execute(getUri());
//	}
    
    public void navigateTo(View view) 
	{
    	ClientLog.d(LOG_TAG, "navigating to");
//    	if (getTasksReady() && getTasks().length() > 0){
//    		String locationStr = (String)view.getTag();
//    		TextView btn = (TextView)view;
//    		
//        	String[] coordinsatesStrArray = locationStr.split(",");
//        	AutoPoint to = new AutoPoint(Double.parseDouble(coordinsatesStrArray[0]), Double.parseDouble(coordinsatesStrArray[1]), btn.getText().toString());
//        	Intent intent;
//			try {
//				intent = AutoMapActivity.getCreationIntent(this, to, getCategoryIdsToTaskContent());
//				startActivity(intent);
//			} catch (JSONException e) {
//				ClientLog.e(LOG_TAG, "Error!", e);
//			}
//        	
//    	}
//    	else{
    		ClientLog.d(LOG_TAG, "tasks are not ready");
    		onWithTheNav(view);
//    	}
    	
	}
    
    
	private void onWithTheNav(View view) {
		
		if (view.getId() == R.id.boxBtn6) {
			
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo://")); 
	    	if (isCallable(intent))
	    	{
	    		startActivity(intent);
	    	}
	    	else
	    	{
	    		Toast.makeText(this, "Go get yourself a map app", Toast.LENGTH_LONG).show();
	    	}
			
	    	return;
	    	
		}
		
		final String locationStr = (String)view.getTag();
		
		ClientLog.d(LOG_TAG, "onWithTheNav with "+locationStr);
		
		if (locationStr == null || locationStr.equals("")) {
			startBoxConfigActivity(view);
		}
		else {
			
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				realyOnWithNav("geo:", locationStr);
				return;
		    }
			
			
//			TextView btn = (TextView)view;
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			
//			boolean storedApp = SettingsManager.getBoolean(this, USE_GOOGLE_MAPS, false);
//			String storedApp = SettingsManager.getString(this, USE_GOOGLE_MAPS);
			String storedApp = sharedPref.getString(MAP_PREFERENCE_KEY, "none");
//			final AutoActivity context = this;
			
			ClientLog.d(LOG_TAG, "storedApp is "+storedApp);
			
			if (storedApp == null || storedApp.equals(getResources().getString(R.string.noneMapSet))) {
				//pop dialog
				final ChooseAppDialog dialog = new ChooseAppDialog(this);
				dialog.setGoogleMapsListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String geoQueryPrefix = "google.navigation:q=";
						
						if (dialog.getNeverAsk()) {
//							SettingsManager.setString(context, USE_GOOGLE_MAPS, GOOGLE_MAPS);
							sharedPref.edit().putString(MAP_PREFERENCE_KEY, GOOGLE_MAPS).commit();
						}
						
						dialog.dismiss();
						realyOnWithNav(geoQueryPrefix, locationStr);
						
					}
				});
				
				dialog.setSysMapsListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						String geoQueryPrefix = "geo:";
						
						ClientLog.d(LOG_TAG, "sys maps clicked");
						
						if (dialog.getNeverAsk()) {
							ClientLog.d(LOG_TAG, "never ask");
//							SettingsManager.setString(context, MAP_PREFERENCE_KEY, "sysDecide");
							sharedPref.edit().putString(MAP_PREFERENCE_KEY, "sysDecide").commit();
						}
						
						dialog.dismiss();
						realyOnWithNav(geoQueryPrefix, locationStr);
						
					}
				});
				dialog.show();
			}
			else {
				
				String geoQueryPrefix = "geo:";
				if (storedApp.equals(GOOGLE_MAPS)) {
					geoQueryPrefix = "google.navigation:q=";
				}
				
				realyOnWithNav(geoQueryPrefix, locationStr);
			}
			
			
		}
		
	}
	
	private void realyOnWithNav(String geoQueryPrefix, String locationStr) {
		
		
		
    	String[] coordinsatesStrArray = locationStr.split(",");
    	AutoPoint point = new AutoPoint(Double.parseDouble(coordinsatesStrArray[0]), Double.parseDouble(coordinsatesStrArray[1]), "getItFromSM!");
    	String geoQuery = geoQueryPrefix+point.getLat()+","+point.getLon();
    	Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(geoQuery)); 
    	if (isCallable(intent))
    	{
    		startActivity(intent);
    	}
    	else
    	{
    		Toast.makeText(this, "Go get yourself a map app", Toast.LENGTH_LONG).show();
    	}
		
	}


	private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 
            PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


	private HashMap<String, String> getCategoryIdsToTaskContent() throws JSONException {
		
		HashMap<String, String> result = new HashMap<String, String>();
		JSONArray tasks = getTasks();
		for (int i = 0; i < tasks.length(); i++){
			JSONObject task = tasks.getJSONObject(i);
			JSONArray categories = task.getJSONArray("categories");
			
			for (int j = 0; j < categories.length(); j++){
				result.put(categories.getJSONObject(j).getString("id"), task.getString("content"));
			}
			
		}
		
		ClientLog.d(LOG_TAG, StringUtils.printMap(result));
		return result;
	}

	public Uri getUri() {
		return mUri;
	}

	public void setUri(Uri uri) {
		mUri = uri;
	}

	public JSONArray getTasks() {
		return mTasks;
	}

	public void setTasks(JSONArray tasks) {
		mTasks = tasks;
		setTasksReady(true);
		Toast.makeText(this, "tasks are ready", Toast.LENGTH_LONG).show();
	}

	public Boolean getTasksReady() {
		return mTasksReady;
	}

	public void setTasksReady(Boolean tasksReady) {
		mTasksReady = tasksReady;
	}
}