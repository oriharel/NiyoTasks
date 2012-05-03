package com.niyo.categories;

import java.io.Serializable;

public class CategoryBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
