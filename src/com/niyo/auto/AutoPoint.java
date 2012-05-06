package com.niyo.auto;

import java.io.Serializable;

public class AutoPoint implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Double mLat;
	private Double mLon;
	private String mName;
	public AutoPoint(Double lat, Double lon, String name)
	{
		setLat(lat);
		setLon(lon);
		setName(name);
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
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		mName = name;
	}
}
