package com.niyo.auto;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.MapFragment;
import com.niyo.ClientLog;
import com.niyo.R;
import com.niyo.SettingsManager;
import com.niyo.auto.map.NiyoMapActivity;
import com.niyo.auto.map.PopupAdapter;

public class CreateAutoBoxAcitivty extends NiyoMapActivity {

	private static final String LOG_TAG = CreateAutoBoxAcitivty.class.getSimpleName();
	private static final String BOX_TITLE_KEY_EXTRA = "titleExtra";
	private static final String BOX_TITLE_DEFAULT_EXTRA = "titleDefaultExtra";
	private static final String BOX_LAT_EXTRA = "latExtra";
	private static final String BOX_LON_EXTRA = "lonExtra";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.create_auto_box_layout);
		setTitle("Create A Destination");
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
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//	    MenuInflater inflater = getMenuInflater();
//	    inflater.inflate(R.menu.actions, menu);
//	    return true;
//	}

	private void setupEditMode() {
		
		Float lat = SettingsManager.getFloat(this, getLatKey(), 0);
		Float lon = SettingsManager.getFloat(this, getLonKey(), 0);
		
		ClientLog.d(LOG_TAG, "lat = "+lat);
		ClientLog.d(LOG_TAG, "lon = "+lon);
		
		if (!(lat == 0 || lon == 0)){
			
			Double latDbl = lat.doubleValue();
			Double lonDbl = lon.doubleValue();
//			LatLng latLbg = new LatLng(latDbl, lonDbl);
			
//			final GoogleMap map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
			Geocoder coder = new Geocoder(this, Locale.getDefault());
			EditText addressEdit = (EditText)findViewById(R.id.boxSearchEdit);
			try {
				List<Address> currentAddress = coder.getFromLocation(latDbl, lonDbl, 5);
//				for (Address address : currentAddress) {
					
//					showAddressResults(currentAddress);
					
					Address selectedAddress = currentAddress.get(0);
					StringBuffer addressName = new StringBuffer();
					addressName.append(selectedAddress.getAddressLine(0)+" ");
					addressName.append(selectedAddress.getAddressLine(1)+" ");
					addressName.append(selectedAddress.getAddressLine(2)+" ");
					ClientLog.d(LOG_TAG, "edit address "+addressName);
					addressEdit.setText(addressName);
//				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ClientLog.e(LOG_TAG, "can't extract address ", e);
			}
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
		}
		
		
		
		
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

	private String getTitleDefault() {
		return getIntent().getStringExtra(BOX_TITLE_DEFAULT_EXTRA);
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
		map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
		
	}

	public static Intent getCreationIntent(Activity acitivity, String titleKey, String latKey, String lonKey, String titleDefault){
		
		Intent intent = new Intent(acitivity, CreateAutoBoxAcitivty.class);
		intent.putExtra(BOX_TITLE_KEY_EXTRA, titleKey);
		intent.putExtra(BOX_LAT_EXTRA, latKey);
		intent.putExtra(BOX_LON_EXTRA, lonKey);
		intent.putExtra(BOX_TITLE_DEFAULT_EXTRA, titleDefault);
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

	@Override
	protected OnInfoWindowClickListener getOnInfoClickListener() {
		
		final CreateAutoBoxAcitivty context = this;
		
		OnInfoWindowClickListener result = new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker marker) {
				
				LatLng ltLng = marker.getPosition();
				Double latDbl = ltLng.latitude;
				Double lonDbl = ltLng.longitude;
				ClientLog.d(LOG_TAG, "setting "+latDbl+" and "+lonDbl+" for key "+getLatKey()+" and "+getLonKey());
				SettingsManager.setFloat(CreateAutoBoxAcitivty.this, getLatKey(), latDbl.floatValue());
				SettingsManager.setFloat(CreateAutoBoxAcitivty.this, getLonKey(), lonDbl.floatValue());
				SettingsManager.setBoolean(CreateAutoBoxAcitivty.this, AutoActivity.SIX_PACK_CHANGED, true);
//				context.finish();
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Destination Name");
				View dialogView = context.getLayoutInflater().inflate(R.layout.box_name_dialog, null);
				final EditText nameEdit = (EditText)dialogView.findViewById(R.id.username);
				nameEdit.setText(getTitleDefault());
				builder.setView(dialogView);
				
				builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   
			        	   ClientLog.d(LOG_TAG, "setting "+nameEdit.getText());
			        	   SettingsManager.setString(CreateAutoBoxAcitivty.this, getTitleKey(), nameEdit.getText().toString());
			               dialog.dismiss();
			        	   context.finish();
			           }
			       });
				builder.setNegativeButton(R.string.cancel_button_label, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               dialog.cancel();
			           }
			       });
				AlertDialog dialog = builder.create();
				dialog.show();
			}

		};
		
		return result;
	}

	protected InfoWindowAdapter getInfoWindowAdapter() {
		
		InfoWindowAdapter result = new InfoWindowAdapter() {
			
			@Override
			public View getInfoWindow(Marker marker) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public View getInfoContents(Marker marker) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		return result;
	}

	
}
