package com.niyo.foursquare;

import java.util.List;

public class FoursquareVenue {

	private String mName;
	private String mId;
	private List<FoursquareVenue> mSubVenues;
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		mName = name;
	}
	public String getId() {
		return mId;
	}
	public void setId(String id) {
		mId = id;
	}
	public List<FoursquareVenue> getSubVenues() {
		return mSubVenues;
	}
	public void setSubVenues(List<FoursquareVenue> subVenues) {
		mSubVenues = subVenues;
	}
}
