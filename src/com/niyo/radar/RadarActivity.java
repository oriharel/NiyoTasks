package com.niyo.radar;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.niyo.ClientLog;
import com.niyo.NiyoApplication;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.SettingsManager;
import com.niyo.network.GenericHttpRequestTask;
import com.niyo.network.NetworkUtilities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RadarActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{
	
	private static final String LOG_TAG = RadarActivity.class.getSimpleName();
	
	private static final int AUTHENTICATE = 0;
	private static final int REGISTER = 1;
	private static final int REFRESH = 2;
	private static final int FRIENDS = 3;

	public static final String USER_EMAIL = "user_email";
	private boolean mToggleIndeterminate = false;
	private AccountManager mAccountManager;
	private LocationClient mLocationClient;
	
	AtomicInteger msgId = new AtomicInteger();
	RadarBroadcastReceiver mRadarRec;
	String mRegid;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.radar_layout);
		mRadarRec = new RadarBroadcastReceiver(this);
		GoogleMap map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		
//		gcm = GoogleCloudMessaging.getInstance(this);
		mLocationClient = new LocationClient(this, this, this);
		
		findViewById(R.id.send).setVisibility(View.GONE);
		findViewById(R.id.clear).setVisibility(View.GONE);
		findViewById(R.id.gcmText).setVisibility(View.GONE);
		
//		findViewById(R.id.send).setOnClickListener(this);
//		findViewById(R.id.clear).setOnClickListener(this);
		
		TextView gcmText = (TextView)findViewById(R.id.gcmText);
		String regIs = SettingsManager.getString(this, NiyoApplication.PROPERTY_REG_ID);
		gcmText.setText(regIs);
		
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }
	
	@Override
	protected void onResume(){
		super.onResume();
		registerReceiver(mRadarRec, new IntentFilter("com.niyo.updateFriend"));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mRadarRec);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) 
	{
		ClientLog.d(LOG_TAG, "onCreateOptionsMenu started");
		
		String imageUrl = SettingsManager.getString(this, GoogleAuthTask.PROFILE_IMAGE_URL);
		if (TextUtils.isEmpty(imageUrl)) {
			menu.add(0, AUTHENTICATE, 0, "Sign in");
		}
		
//		menu.add(0, REGISTER, 0, "Register");
		
		if (!mToggleIndeterminate){
			MenuItem refreshMI = menu.add(0, REFRESH, 0, "Refresh");
			refreshMI.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			refreshMI.setIcon(R.drawable.ic_menu_refresh);
		}
		
		MenuItem friendsMI = menu.add(0, FRIENDS, 0, "Friends");
		friendsMI.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		friendsMI.setIcon(R.drawable.ic_menu_allfriends);
		
		
		return true;
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) 
	{
    	if (menuItem.getItemId() == AUTHENTICATE){
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		
    		final RadarActivity context = this;
    		final String[] namesArray = getAccountNames();
    		final int[] selectedIndex = new int[1];
    		builder.setTitle("Choose an account");
			builder.setSingleChoiceItems(namesArray, 0, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					try {
						selectedIndex[0] = which;
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ClientLog.e(LOG_TAG, "Error! "+e);
					}
					
				}
			});
			
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int id) {
					
					final String userEmail = namesArray[selectedIndex[0]];
					SettingsManager.setString(context, USER_EMAIL, userEmail);
					
					GoogleAuthTask task = new GoogleAuthTask(context, new ServiceCaller() {
						
						@Override
						public void success(Object data) {
							registerWithServer(SettingsManager.getString(context, GoogleAuthTask.PROFILE_ID), userEmail);
							
						}
						
						@Override
						public void failure(Object data, String description) {
							// TODO Auto-generated method stub
							
						}
					}, dialog, userEmail);
					task.execute();
					
				}
			});
			
			builder.setNegativeButton(R.string.cancel_button_label, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               dialog.cancel();
		           }
		       });
			AlertDialog dialog = builder.create();
			dialog.show();
    		
        	return true;
    	}
    	else if (menuItem.getItemId() == REGISTER) {
    		registerWithServer(SettingsManager.getString(this, GoogleAuthTask.PROFILE_ID), SettingsManager.getString(this, USER_EMAIL));
    		return true;
    	}
    	
    	else if (menuItem.getItemId() == REFRESH) {
    		requestLocationsUpdate();
    		return true;
    	}
    	else{
    		return false;
    	}
    	
	}
	
	private void requestLocationsUpdate() {
		
		String askee = "yifat.ferber@gmail.com";
//		String askee = "ori.harel@gmail.com";
//		String askee = "inbal.2705@gmail.com";
//		String askee = "sendwithchibo@gmail.com";
		String email = SettingsManager.getString(this, USER_EMAIL);
		UUID trxId = UUID.randomUUID(); 
		
		String url = NetworkUtilities.BASE_URL+"/askForPosition?user_asking="+email+"&user_answering="+askee+"&trx_id="+trxId;
		ClientLog.d(LOG_TAG, "asking for position to niyo server with "+url);
		final RadarActivity context = this;
		ServiceCaller caller = new ServiceCaller() {
			
			@Override
			public void success(Object data) {
				
				
			}
			
			@Override
			public void failure(Object data, String description) {
				
				Toast.makeText(context, description, Toast.LENGTH_LONG).show();
				toggleProgressBar();
				
			}
		};
		GenericHttpRequestTask httpTask = new GenericHttpRequestTask(caller);
		httpTask.execute(url);
		toggleProgressBar();
	}
	
	private void toggleProgressBar() {
		mToggleIndeterminate = !mToggleIndeterminate;
        setProgressBarIndeterminateVisibility(mToggleIndeterminate);
        invalidateOptionsMenu();
	}

	protected void registerWithServer(String userId, String userEmail) {
		
		String reg_id = SettingsManager.getString(this, NiyoApplication.PROPERTY_REG_ID);
		
		if (TextUtils.isEmpty(reg_id) || TextUtils.isEmpty(userEmail)) {
			
			ClientLog.w(LOG_TAG, "unable to register to server. no gcm reg id or user email");
			Toast.makeText(this, "Can't register to server because couldn't retrieve gcm id", Toast.LENGTH_LONG).show();
		}
		else {
			String url = NetworkUtilities.BASE_URL+"/register?reg_id="+reg_id+"&user_id="+userId+"&user_email="+userEmail;
			ClientLog.d(LOG_TAG, "registering to niyo server with "+url);
			final RadarActivity context = this;
			ServiceCaller caller = new ServiceCaller() {
				
				@Override
				public void success(Object data) {
					ClientLog.d(LOG_TAG, "registration succeeded");
					Toast.makeText(context, "Registration to NIYO server succeeded", Toast.LENGTH_LONG).show();
					
				}
				
				@Override
				public void failure(Object data, String description) {
					// TODO Auto-generated method stub
					
				}
			};
			GenericHttpRequestTask httpTask = new GenericHttpRequestTask(caller);
			httpTask.execute(url);
		}
	}

	private String[] getAccountNames() {
        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String[] names = new String[accounts.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = accounts[i].name;
        }
        return names;
    }
	
	
	
	public static Intent getCreationIntent(Activity acitivity){
		
		Intent intent = new Intent(acitivity, RadarActivity.class);
		return intent;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		
		
		Location location = mLocationClient.getLastLocation();
		
		if (location != null){
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
			
			GoogleMap map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
			map.animateCamera(update);
		}
		
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	public class FriendImageFetcher extends AsyncTask<String, Void, Void>
	{
		private final String LOG_TAG = FriendImageFetcher.class.getSimpleName();
		private ImageView mImageView;
		private Bitmap mBitMap;
		private String mEmail;
		private Double mLat;
		private Double mLon;
		private String mMsg;
		
		public FriendImageFetcher(ImageView imageView, String email, Double lat, Double lon, String msg) {
			
			mImageView = imageView;
			mEmail = email;
			mLat = lat;
			mLon = lon;
			mMsg = msg;
		}

		@Override
		protected Void doInBackground(String... params) {
			
			URL url;
			try {
				url = new URL(params[0]);
				ClientLog.d(LOG_TAG, "do in backgroudn started with url "+params[0]);
			
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();   
				conn.setDoInput(true);   
				conn.connect();     
				InputStream is = conn.getInputStream();
				mBitMap = BitmapFactory.decodeStream(is); 
			
			} catch (Exception e) {
				e.printStackTrace();
				
				ClientLog.e(LOG_TAG, "Error in fetching "+params[0]);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
	         mImageView.setImageBitmap(mBitMap);
	         setNiyoMarker(mImageView, mEmail, mLat, mLon, mMsg);
		}
		
	}
	
	protected void setNiyoMarker(ImageView image, String email, Double lat, Double lon, String msg) {
		
		GoogleMap map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		
		MarkerOptions options = new MarkerOptions();
		LatLng position = new LatLng(lat, lon);
		options.position(position);
		LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
		boundsBuilder.include(position);
		
		Location location = mLocationClient.getLastLocation();
		LatLng myPosition = new LatLng(location.getLatitude(), location.getLongitude());
		boundsBuilder.include(myPosition);
		
		options.title(email);
		options.snippet(email+" is here! ("+msg+")");
		
		image.setDrawingCacheEnabled(true);
		image.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 
	            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		image.layout(0, 0, 63, 77); 
	
		image.buildDrawingCache(true);
		
		BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(image.getDrawingCache());
		options.icon(icon);
		map.addMarker(options);
		
		CameraUpdate update = CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 200);
		toggleProgressBar();
		map.animateCamera(update);
	}

	public void updateFriend(String email, Double lat, Double lon, String msg, String imageUrl) {
		
		ClientLog.d(LOG_TAG, "updating friend");
		ImageView image = new NiyoMarker(this);
		
		FriendImageFetcher task = new FriendImageFetcher(image, email, lat, lon, msg);
		task.execute(imageUrl);
		
	}
}
