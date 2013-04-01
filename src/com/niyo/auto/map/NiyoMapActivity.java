package com.niyo.auto.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBoundsCreator;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;

public abstract class NiyoMapActivity extends NiyoAbstractActivity {
	
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
		}
	}
	
	protected void doNoResultsFromCode(String userAddress) {
		
		Toast.makeText(this, "No results for "+userAddress, Toast.LENGTH_LONG).show();
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
			ClientLog.d(LOG_TAG, "address is "+address);
			map.addMarker(new MarkerOptions().position(latLng));
		}
//		for (LatLng latLng : latlngs) {
			
//		}
		
		map.setOnMarkerClickListener(getOnMarkerClickListener());
//		map.setInfoWindowAdapter(getInfoWindowAdapter());
		
		CameraUpdate update = CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 200);
		
		map.animateCamera(update);
		

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

	protected abstract OnMarkerClickListener getOnMarkerClickListener();

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
