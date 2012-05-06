package com.niyo.tasks.map;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.niyo.R;

public class AutoItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private static final String LOG_TAG = AutoItemizedOverlay.class.getSimpleName();
	private ArrayList<OverlayItem> items;
	
	public AutoItemizedOverlay(Drawable defaultMarker) {

		super(boundCenterBottom(defaultMarker));
		items = new ArrayList<OverlayItem>();
		populate();
	}

	public void addNewItem(GeoPoint location, String markerText,
			String snippet) {
		OverlayItem newItem = new OverlayItem(location, markerText, snippet);
		items.add(newItem);
		populate();
	}
	
	public void removeItem(int index) {
		items.remove(index);
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int index) {
		return items.get(index);
	}

	@Override
	public int size() {
		return items.size();
	}
	
//	@Override
//	public boolean onTap(GeoPoint point, MapView mapView){
//		
//		MapView.LayoutParams geoLP = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
//				MapView.LayoutParams.WRAP_CONTENT,
//				point,
//				MapView.LayoutParams.BOTTOM_CENTER);
//		
//		Activity context = (Activity)mapView.getContext();
//		View baloon = context.getLayoutInflater().inflate(R.layout.marker_baloon, null);
//		
//		mapView.addView(baloon, geoLP);
//		return false;
//	}

}
