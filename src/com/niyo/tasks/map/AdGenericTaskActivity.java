package com.niyo.tasks.map;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.niyo.ClientLog;
import com.niyo.R;
import com.niyo.Utils;

public class AdGenericTaskActivity extends MapActivity implements OnClickListener {
	
	private static final String LOG_TAG = AdGenericTaskActivity.class.getSimpleName();
	private Address mSeletctedAddress;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_generic_task_layout);
		findViewById(R.id.searchBtn).setOnClickListener(this);
		EditText searchEdit = (EditText)findViewById(R.id.searchEdit);
		searchEdit.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				
				onClick(v);
				return true;
			}
		});
		
		Utils.showKeyboard2(this);
		findViewById(R.id.createTask).setOnClickListener(getCreateTaskClickListener());
	}
	
	private OnClickListener getCreateTaskClickListener() {
		
		
		
		OnClickListener result = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				TextView taskTitle = (TextView)findViewById(R.id.taskTitleLabel);
				
				if (TextUtils.isEmpty(taskTitle.getText())){
					Toast.makeText(AdGenericTaskActivity.this, "You must enter a task title", Toast.LENGTH_LONG);
					return;
				}
				
			}
		};
		return result;
	}

	public static Intent getCreationIntent(Activity activity){
		
		Intent intent = new Intent(activity, AdGenericTaskActivity.class);
		return intent;
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		Utils.hideKeyboard(this, findViewById(R.id.searchEdit));
	}

	@Override
	public void onClick(View v) {
		
		EditText addressEdit = (EditText)findViewById(R.id.searchEdit);
		Utils.hideKeyboard(this, addressEdit);
		String userAddress = addressEdit.getText().toString();
		
		Geocoder coder = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addresses = coder.getFromLocationName(userAddress, 5);
			
			if (addresses.size() <= 0){
				Toast.makeText(this, "No results for "+userAddress, Toast.LENGTH_LONG).show();
			}
			else{
				if (addresses.size() == 1){
					mSeletctedAddress = addresses.get(0);
				}
				showResults(addresses);
			}
			
		} catch (IOException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
	}

	private void showResults(List<Address> addresses) {
		
		findViewById(R.id.map_view).setVisibility(View.VISIBLE);
		MapView mapView = (MapView)findViewById(R.id.map_view);
		List<Overlay> overlays = mapView.getOverlays();
		Resources r = getResources();
		AutoItemizedOverlay markers = new AutoItemizedOverlay(r.getDrawable(R.drawable.marker));
		for (Address address : addresses) {
			
			Double lat1E6 = new Double(address.getLatitude()*1E6);
			Double lon1E6 = new Double(address.getLongitude()*1E6);
			GeoPoint point = new GeoPoint(lat1E6.intValue(), lon1E6.intValue());
			
			markers.addNewItem(point, "markerText", "snippet");
		}
		overlays.add(markers);
		
		centerAndZoom(addresses);
		
	}

	private void centerAndZoom(List<Address> addresses) {
		
		MapView mapView = (MapView)findViewById(R.id.map_view);
		MapController mapController = mapView.getController();
		
		Double lat1E6 = new Double(addresses.get(0).getLatitude()*1E6);
		Double lon1E6 = new Double(addresses.get(0).getLongitude()*1E6);
		GeoPoint point = new GeoPoint(lat1E6.intValue(), lon1E6.intValue());
		
		mapController.setCenter(point);
		mapController.setZoom(18);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
