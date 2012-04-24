package com.niyo.auto;

public class AutoPoint {
	private Double mLat;
	private Double mLon;
	
	public AutoPoint(Double lat, Double lon)
	{
		setLat(lat);
		setLon(lon);
	}
	public Double getLat() {
		return mLat;
	}
	public void setLat(Double lat) {
		mLat = lat;
	}
	public Double getLon() {
		return mLon;
	}
	public void setLon(Double lon) {
		mLon = lon;
	}
	
	@Override
	public String toString()
	{
		return "Lat: ("+getLat()+"), Lon: ("+getLon()+")";
	}
}
