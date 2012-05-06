package com.niyo.auto;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.niyo.ClientLog;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.SettingsManager;
import com.niyo.auto.map.NiyoMapActivity;
import com.niyo.tasks.map.AddressGeocodingTask;
import com.niyo.tasks.map.AutoItemizedOverlay;

public class CreateAutoBoxAcitivty extends NiyoMapActivity implements OnClickListener {

	private static final String LOG_TAG = CreateAutoBoxAcitivty.class.getSimpleName();
	private static final String BOX_TITLE_KEY_EXTRA = "titleExtra";
	private static final String BOX_LAT_EXTRA = "latExtra";
	private static final String BOX_LON_EXTRA = "lonExtra";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.create_auto_box_layout);
		
		findViewById(R.id.boxSearchBtn).setOnClickListener(this);
		
		setupEditMode();
		
		setupMap();
		
		findViewById(R.id.addBox).setOnClickListener(getSetBoxListener());
	}

	private void setupEditMode() {
		
		TextView title = (TextView)findViewById(R.id.boxTitleEdit);
		title.setText(SettingsManager.getString(this, getTitleKey()));
		
		Float lat = SettingsManager.getFloat(this, getLatKey(), 0);
		Float lon = SettingsManager.getFloat(this, getLonKey(), 0);
		
		ClientLog.d(LOG_TAG, "lat = "+lat);
		ClientLog.d(LOG_TAG, "lon = "+lon);
		
		if (!(lat == 0 || lon == 0)){
			
			Double latDbl = lat*1e6;
			Double lonDbl = lon*1e6;
			mSeletctedAddress = new GeoPoint(latDbl.intValue(), lonDbl.intValue());
			
			MapView mapView = (MapView)findViewById(getMapViewId());
			List<Overlay> overlays = mapView.getOverlays();
			Resources r = getResources();
			AutoItemizedOverlay markers = new AutoItemizedOverlay(r.getDrawable(R.drawable.marker));
			GeoPoint point = new GeoPoint(latDbl.intValue(), lonDbl.intValue());
			markers.addNewItem(point, "markerText", "snippet");
			
			overlays.add(markers);
			
			mapView.getController().setCenter(point);
			mapView.getController().setZoom(18);
			
			ServiceCaller caller = new ServiceCaller() {

				@Override
				public void success(Object data) {

					if (data != null){

						List<Address> addresses = (List<Address>)data;
						if (addresses != null && addresses.size() > 0){

							EditText addressSearch = (EditText)findViewById(R.id.boxSearchEdit);
							addressSearch.setText(addresses.get(0).getAddressLine(0));
						}
					}
				}

				@Override
				public void failure(Object data, String description) {
					// TODO Auto-generated method stub

				}
			};
			
			AddressGeocodingTask task = new AddressGeocodingTask(this, caller);
			Double[] params = new Double[2];
			params[0] = lat.doubleValue();
			params[1] = lon.doubleValue();
			task.execute(params);
		}
		
	}

	private OnClickListener getSetBoxListener() {
		
		OnClickListener result = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				TextView title = (TextView)findViewById(R.id.boxTitleEdit);
				String titleStr = title.getText().toString();
				Double latDbl = null;
				Double lonDbl = null;
				if (mSeletctedAddress != null){
					latDbl = mSeletctedAddress.getLatitudeE6()/1e6;
					lonDbl = mSeletctedAddress.getLongitudeE6()/1e6;
					SettingsManager.setFloat(CreateAutoBoxAcitivty.this, getLatKey(), latDbl.floatValue());
					SettingsManager.setFloat(CreateAutoBoxAcitivty.this, getLonKey(), lonDbl.floatValue());
				}
				
				SettingsManager.setString(CreateAutoBoxAcitivty.this, getTitleKey(), titleStr);
				SettingsManager.setBoolean(CreateAutoBoxAcitivty.this, AutoActivity.SIX_PACK_CHANGED, true);
				finish();
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
		
		MapView mapView = (MapView)findViewById(R.id.boxMapView);
		
		mapView.displayZoomControls(true);
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public static Intent getCreationIntent(Activity acitivity, String titleKey, String latKey, String lonKey){
		
		Intent intent = new Intent(acitivity, CreateAutoBoxAcitivty.class);
		intent.putExtra(BOX_TITLE_KEY_EXTRA, titleKey);
		intent.putExtra(BOX_LAT_EXTRA, latKey);
		intent.putExtra(BOX_LON_EXTRA, lonKey);
		return intent;
	}

	@Override
	public void onClick(View v) {
		
		EditText addressEdit = (EditText)findViewById(R.id.boxSearchEdit);
		String userAddress = addressEdit.getText().toString();
		
		showMarker(userAddress);
	}

	@Override
	protected int getMapViewId() {
		return R.id.boxMapView;
	}
	
	
}
