package com.niyo.categories;

import org.json.JSONException;
import org.json.JSONObject;

import com.niyo.tasks.TaskJsonObject;

public class CategoryJsonObject extends JSONObject {
	
private static final String LOG_TAG = TaskJsonObject.class.getSimpleName();
	
	public CategoryJsonObject(JSONObject jsonObject) throws JSONException {
		
		super(jsonObject, new String[]{"category", "tasks"});
	}
}
