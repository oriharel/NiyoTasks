package com.niyo.auto;

public class AutoVenue implements Comparable<AutoVenue>{
	private String mName;
	private Double mDistance;
	private AutoPoint mLocation;
	private String mFoursqaureId;
	private String mAddress;
	private String mTaskContent;
	
	public AutoVenue(String name, AutoPoint location, String foursquareId, String address, String taskContent)
	{
		setName(name);
		setLocation(location);
		setFoursqaureId(foursquareId);
		setAddress(address);
		setTaskContent(taskContent);
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public Double getDistance() {
		return mDistance;
	}

	public void setDistance(Double distance) {
		mDistance = distance;
	}

	@Override
	public int compareTo(AutoVenue another) {
		if (getDistance() > another.getDistance()){
			return -1;
		}
		else if (getDistance() < another.getDistance()){
			return 1;
		}
		return 0;
	}

	public AutoPoint getLocation() {
		return mLocation;
	}

	public void setLocation(AutoPoint location) {
		mLocation = location;
	}

	public String getFoursqaureId() {
		return mFoursqaureId;
	}

	public void setFoursqaureId(String foursqaureId) {
		mFoursqaureId = foursqaureId;
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String address) {
		mAddress = address;
	}

	public String getTaskContent() {
		return mTaskContent;
	}

	public void setTaskContent(String taskContent) {
		mTaskContent = taskContent;
	}
}
