package com.niyo.auto.map;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.niyo.ClientLog;
import com.niyo.R;
import com.niyo.tasks.map.AutoItemizedOverlay;

public abstract class NiyoMapActivity extends MapActivity {
	
	private static String LOG_TAG = NiyoMapActivity.class.getSimpleName();
	protected GeoPoint mSeletctedAddress;

	protected void showMarker(String userAddress) {
		Geocoder coder = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addresses = coder.getFromLocationName(userAddress, 5);
			
			if (addresses.size() <= 0){
				Toast.makeText(this, "No results for "+userAddress, Toast.LENGTH_LONG).show();
			}
			else{
				if (addresses.size() == 1){
					mSeletctedAddress = createGeoPoint(addresses.get(0));
				}
				showResults(addresses);
			}
			
		} catch (IOException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
	}
	
	private void showResults(List<Address> addresses) {

		MapView mapView = (MapView)findViewById(getMapViewId());
		List<Overlay> overlays = mapView.getOverlays();
		Resources r = getResources();
		AutoItemizedOverlay markers = new AutoItemizedOverlay(r.getDrawable(R.drawable.marker));
		ClientLog.d(LOG_TAG, "got "+addresses.size()+" address");
		for (Address address : addresses) {

			Double lat1E6 = new Double(address.getLatitude()*1E6);
			Double lon1E6 = new Double(address.getLongitude()*1E6);
			GeoPoint point = new GeoPoint(lat1E6.intValue(), lon1E6.intValue());

			markers.addNewItem(point, "markerText", "snippet");
		}
		overlays.add(markers);

		centerAndZoom(addresses);
	}
	
	protected abstract int getMapViewId();

	protected void centerAndZoom(List<Address> addresses) {

		MapView mapView = (MapView)findViewById(getMapViewId());
		MapController mapController = mapView.getController();

		Double lat1E6 = new Double(addresses.get(0).getLatitude()*1E6);
		Double lon1E6 = new Double(addresses.get(0).getLongitude()*1E6);
		GeoPoint point = new GeoPoint(lat1E6.intValue(), lon1E6.intValue());

		mapController.setCenter(point);
		mapController.setZoom(18);
	}

	
	private GeoPoint createGeoPoint(Address address) {

		Double lat1e6 = new Double(address.getLatitude()*1e6);
		Double lon1e6 = new Double(address.getLongitude()*1e6);

		GeoPoint result = new GeoPoint(lat1e6.intValue(), lon1e6.intValue());
		return result;
	}
}
