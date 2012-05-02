package com.niyo.categories;

public class CategoryBean {

	private String mName;
	private String mId;
	
	public CategoryBean(String name, String id){
		setName(name);
		setId(id);
	}

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
}
