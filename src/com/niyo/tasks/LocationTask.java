//package com.niyo.tasks;
//
//import java.io.Serializable;
//import java.util.List;
//
//import com.google.android.maps.GeoPoint;
//import com.niyo.categories.CategoryBean;
//
//public class LocationTask implements Serializable{
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	private GeoPoint mPoint;
//	private String mTitle;
//	private Double mDoublelat;
//	private Double mDoubleLon;
//	private String mUserInputAddress;
//	private String mId;
//	private List<CategoryBean> mCategories;
//	
//	public LocationTask(String id, GeoPoint point, String title, String userInputAddress, List<CategoryBean> categories){
//		setPoint(point);
//		setTitle(title);
//		
//		if (point != null){
//			setDoublelat(point.getLatitudeE6()/1e6);
//			setDoubleLon(point.getLongitudeE6()/1e6);
//		}
//		setId(id);
//		setUserInputAddress(userInputAddress);
//		setCategories(categories);
//	}
//	public GeoPoint getPoint() {
//		return mPoint;
//	}
//	public void setPoint(GeoPoint point) {
//		mPoint = point;
//	}
//	public String getTitle() {
//		return mTitle;
//	}
//	public void setTitle(String title) {
//		mTitle = title;
//	}
//	public Double getDoublelat() {
//		return mDoublelat;
//	}
//	public void setDoublelat(Double doublelat) {
//		mDoublelat = doublelat;
//	}
//	public Double getDoubleLon() {
//		return mDoubleLon;
//	}
//	public void setDoubleLon(Double doubleLon) {
//		mDoubleLon = doubleLon;
//	}
//	public String getUserInputAddress() {
//		return mUserInputAddress;
//	}
//	public void setUserInputAddress(String userInputAddress) {
//		mUserInputAddress = userInputAddress;
//	}
//	public String getId() {
//		return mId;
//	}
//	public void setId(String id) {
//		mId = id;
//	}
//	public List<CategoryBean> getCategories() {
//		return mCategories;
//	}
//	public void setCategories(List<CategoryBean> categories) {
//		mCategories = categories;
//	}
//}
