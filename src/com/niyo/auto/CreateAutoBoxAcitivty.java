package com.niyo.auto;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.niyo.R;
import com.niyo.SettingsManager;
import com.niyo.auto.map.NiyoMapActivity;

public class CreateAutoBoxAcitivty extends NiyoMapActivity {

	private static final String LOG_TAG = CreateAutoBoxAcitivty.class.getSimpleName();
	private static final String BOX_TITLE_KEY_EXTRA = "titleExtra";
	private static final String BOX_LAT_EXTRA = "latExtra";
	private static final String BOX_LON_EXTRA = "lonExtra";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.create_auto_box_layout);
		final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.DISPLAY_HOME_AS_UP);
		
		findViewById(R.id.boxSearchBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				EditText addressEdit = (EditText)findViewById(R.id.boxSearchEdit);
				String userAddress = addressEdit.getText().toString();
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				showMarker(userAddress);
				
			}
		});
		
		setupEditMode();
		
		setupMap();
		
//		findViewById(R.id.addBox).setOnClickListener(getSetBoxListener());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.actions, menu);
	    return true;
	}

	private void setupEditMode() {
		
//		TextView title = (TextView)findViewById(R.id.boxTitleEdit);
//		title.setText(SettingsManager.getString(this, getTitleKey()));
//		
//		Float lat = SettingsManager.getFloat(this, getLatKey(), 0);
//		Float lon = SettingsManager.getFloat(this, getLonKey(), 0);
//		
//		ClientLog.d(LOG_TAG, "lat = "+lat);
//		ClientLog.d(LOG_TAG, "lon = "+lon);
//		
//		if (!(lat == 0 || lon == 0)){
//			
//			Double latDbl = lat*1e6;
//			Double lonDbl = lon*1e6;
//			mSeletctedAddress = new GeoPoint(latDbl.intValue(), lonDbl.intValue());
//			
//			MapView mapView = (MapView)findViewById(getMapViewId());
//			List<Overlay> overlays = mapView.getOverlays();
//			Resources r = getResources();
//			AutoItemizedOverlay markers = new AutoItemizedOverlay(r.getDrawable(R.drawable.marker));
//			GeoPoint point = new GeoPoint(latDbl.intValue(), lonDbl.intValue());
//			markers.addNewItem(point, "markerText", "snippet");
//			
//			overlays.add(markers);
//			
//			mapView.getController().setCenter(point);
//			mapView.getController().setZoom(18);
//			
//			ServiceCaller caller = new ServiceCaller() {
//
//				@Override
//				public void success(Object data) {
//
//					if (data != null){
//
//						List<Address> addresses = (List<Address>)data;
//						if (addresses != null && addresses.size() > 0){
//
//							EditText addressSearch = (EditText)findViewById(R.id.boxSearchEdit);
//							addressSearch.setText(addresses.get(0).getAddressLine(0));
//						}
//					}
//				}
//
//				@Override
//				public void failure(Object data, String description) {
//					// TODO Auto-generated method stub
//
//				}
//			};
//			
//			AddressGeocodingTask task = new AddressGeocodingTask(this, caller);
//			Double[] params = new Double[2];
//			params[0] = lat.doubleValue();
//			params[1] = lon.doubleValue();
//			task.execute(params);
//		}
		
	}

	private OnClickListener getSetBoxListener() {
		
		OnClickListener result = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
//				TextView title = (TextView)findViewById(R.id.boxTitleEdit);
//				String titleStr = title.getText().toString();
				Double latDbl = null;
				Double lonDbl = null;
//				if (mSeletctedAddress != null){
//					latDbl = mSeletctedAddress.getLatitudeE6()/1e6;
//					lonDbl = mSeletctedAddress.getLongitudeE6()/1e6;
//					SettingsManager.setFloat(CreateAutoBoxAcitivty.this, getLatKey(), latDbl.floatValue());
//					SettingsManager.setFloat(CreateAutoBoxAcitivty.this, getLonKey(), lonDbl.floatValue());
//				}
//				
//				SettingsManager.setString(CreateAutoBoxAcitivty.this, getTitleKey(), titleStr);
//				SettingsManager.setBoolean(CreateAutoBoxAcitivty.this, AutoActivity.SIX_PACK_CHANGED, true);
//				finish();
			}
		};
				
		return result;
	}

	private String getTitleKey() {
		return getIntent().getStringExtra(BOX_TITLE_KEY_EXTRA);
	}
	
	private String getLatKey(){
		return getIntent().getStringExtra(BOX_LAT_EXTRA);
	}
	
	private String getLonKey(){
		return getIntent().getStringExtra(BOX_LON_EXTRA);
	}

	private void setupMap() {
		
		GoogleMap map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		map.getUiSettings().setZoomControlsEnabled(true);
		
	}

	public static Intent getCreationIntent(Activity acitivity, String titleKey, String latKey, String lonKey){
		
		Intent intent = new Intent(acitivity, CreateAutoBoxAcitivty.class);
		intent.putExtra(BOX_TITLE_KEY_EXTRA, titleKey);
		intent.putExtra(BOX_LAT_EXTRA, latKey);
		intent.putExtra(BOX_LON_EXTRA, lonKey);
		return intent;
	}

	public void onClick(View v) {
		
//		EditText addressEdit = (EditText)findViewById(R.id.boxSearchEdit);
//		String userAddress = addressEdit.getText().toString();
//		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//		showMarker(userAddress);
	}

	@Override
	protected int getMapViewId() {
		return R.id.map;
	}

	
}
