package com.niyo.tasks;

import org.json.JSONException;
import org.json.JSONObject;

import com.niyo.ClientLog;

public class TaskJsonObject extends JSONObject {
	
	private static final String LOG_TAG = TaskJsonObject.class.getSimpleName();
	
	public TaskJsonObject(JSONObject jsonObject) throws JSONException {
		
		super(jsonObject, new String[]{"content", "category", "taskId"});
	}

	@Override
	public String toString(){
		ClientLog.d(LOG_TAG, "toSTring is called");
		try {
			return getString("content");
		} catch (JSONException e) {
			ClientLog.w(LOG_TAG, "Warning!", e);
			return super.toString();
		}
	}
}
