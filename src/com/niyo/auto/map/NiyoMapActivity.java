package com.niyo.auto.map;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;

import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.niyo.ClientLog;
import com.niyo.R;

public abstract class NiyoMapActivity extends Activity {
	
	private static String LOG_TAG = NiyoMapActivity.class.getSimpleName();
	
	protected final Handler pHandler = new Handler();
	
	protected Handler getHandler() {
		return pHandler;
	}

	protected void showMarker(String userAddress) {
		Geocoder coder = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addresses = coder.getFromLocationName(userAddress, 5);
			
			if (addresses.size() <= 0){
				
				doNoResultsFromCode(userAddress);
				
			}
			else{
				
					
				showAddressResults(addresses);
			}
			
		} catch (IOException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
			Toast.makeText(this, "Can't resolve address at this time. please reboot device or add it manually ("+e.getMessage()+")", Toast.LENGTH_LONG).show();
		}
	}
	
	protected void doNoResultsFromCode(String userAddress) {
		
		Toast.makeText(this, "No results for "+userAddress, Toast.LENGTH_LONG).show();
	}
	
	protected String getAddressString(Address selectedAddress) {
		StringBuffer addressName = new StringBuffer();
		addressName.append(selectedAddress.getAddressLine(0)+" ");
		addressName.append(selectedAddress.getAddressLine(1)+" ");
		addressName.append(selectedAddress.getAddressLine(2)+" ");
		
		return addressName.toString();
	}

	protected void showAddressResults(List<Address> addresses) {
		
		
		final GoogleMap map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
		
//		List<LatLng> latlngs = new ArrayList<LatLng>();
		for (Address address : addresses) {
			double lat = address.getLatitude();
			double lon = address.getLongitude();
			LatLng latLng = new LatLng(lat, lon);
//			latlngs.add(new LatLng(lat, lon));
			boundsBuilder.include(latLng);
			String addressToShow = getAddressString(address);
			ClientLog.d(LOG_TAG, "getAddressString(address) is "+addressToShow);
			map.addMarker(new MarkerOptions().position(latLng).title(addressToShow).snippet("Tap here tp add!"));
		}
//		for (LatLng latLng : latlngs) {
			
//		}
		
//		map.setOnMarkerClickListener(getOnMarkerClickListener());
		
//		map.setInfoWindowAdapter(getInfoWindowAdapter());
		
		if (addresses.size() > 1) {
			CameraUpdate update = CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 200);
			map.animateCamera(update);
		}
		else if (addresses.size() == 1){
			
			Address address = addresses.get(0);
			LatLng ltLng = new LatLng(address.getLatitude(), address.getLongitude());
			CameraUpdate update = CameraUpdateFactory.newLatLng(ltLng);
			map.animateCamera(update, new GoogleMap.CancelableCallback() {
				
				@Override
				public void onFinish() {
					CameraUpdate update = CameraUpdateFactory.zoomTo(15);
					map.animateCamera(update);
					
				}
				
				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		
		
		

//		MapView mapView = (MapView)findViewById(getMapViewId());
//		List<Overlay> overlays = mapView.getOverlays();
//		Resources r = getResources();
//		AutoItemizedOverlay markers = new AutoItemizedOverlay(r.getDrawable(R.drawable.marker));
//		markers.setActivity((AdGenericTaskActivity)this);
//		ClientLog.d(LOG_TAG, "got "+addresses.size()+" address");
//		for (Address address : addresses) {
//
//			Double lat1E6 = Double.valueOf(address.getLatitude()*1E6);
//			Double lon1E6 = Double.valueOf(address.getLongitude()*1E6);
//			GeoPoint point = new GeoPoint(lat1E6.intValue(), lon1E6.intValue());
//
//			markers.addNewItem(point, "markerText", "snippet");
//		}
//		overlays.add(markers);
//
//		centerAndZoom(addresses);
	}
	
//	protected abstract InfoWindowAdapter getInfoWindowAdapter();

	protected abstract OnInfoWindowClickListener getOnInfoClickListener();

	protected abstract int getMapViewId();

	protected void centerAndZoom(List<Address> addresses) {

//		MapView mapView = (MapView)findViewById(getMapViewId());
//		MapController mapController = mapView.getController();
//
//		Double lat1E6 = Double.valueOf(addresses.get(0).getLatitude()*1E6);
//		Double lon1E6 = Double.valueOf(addresses.get(0).getLongitude()*1E6);
//		GeoPoint point = new GeoPoint(lat1E6.intValue(), lon1E6.intValue());
//
//		mapController.setCenter(point);
//		mapController.setZoom(18);
	}

	
//	private GeoPoint createGeoPoint(Address address) {

//		Double lat1e6 = Double.valueOf(address.getLatitude()*1e6);
//		Double lon1e6 = Double.valueOf(address.getLongitude()*1e6);
//
//		GeoPoint result = new GeoPoint(lat1e6.intValue(), lon1e6.intValue());
//		return null;
//	}
}
