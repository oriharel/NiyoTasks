package com.niyo.tasks;

import com.google.android.maps.GeoPoint;

public class LocationTask {

	private GeoPoint mPoint;
	private String mTitle;
	private Double mDoublelat;
	private Double mDoubleLon;
	
	public LocationTask(GeoPoint point, String title){
		setPoint(point);
		setTitle(title);
		
		setDoublelat(point.getLatitudeE6()/1e6);
		setDoubleLon(point.getLongitudeE6()/1e6);
	}
	public GeoPoint getPoint() {
		return mPoint;
	}
	public void setPoint(GeoPoint point) {
		mPoint = point;
	}
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		mTitle = title;
	}
	public Double getDoublelat() {
		return mDoublelat;
	}
	public void setDoublelat(Double doublelat) {
		mDoublelat = doublelat;
	}
	public Double getDoubleLon() {
		return mDoubleLon;
	}
	public void setDoubleLon(Double doubleLon) {
		mDoubleLon = doubleLon;
	}
}
