package com.niyo.categories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.niyo.ClientLog;
import com.niyo.ServiceCaller;
import com.niyo.Utils;

public class DeleteCategoryTask extends AsyncTask<String, Void, Boolean> {

	private static final String LOG_TAG = DeleteCategoryTask.class.getSimpleName();
	private Context mContext;
	
	public DeleteCategoryTask(Context context, ServiceCaller caller){
		
		mContext = context;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {

		JSONObject result = Utils.getTasksFromProvider(mContext);
		
		try {

			JSONArray tasks = null;
			if (result == null){
				
				ClientLog.e(LOG_TAG, "you delete a task when there are no tasks?!");
			}
			else{
				tasks = result.getJSONArray("tasks");
				
				String categoryId = params[0];
				JSONArray newCategories = new JSONArray();
				
				for (int i = 0; i < tasks.length(); i++){
					
					JSONObject currTask = tasks.getJSONObject(i);
					
					JSONArray categories = currTask.getJSONArray("categories");
					
					for (int j = 0; j < categories.length(); j++){
						
						JSONObject currCat = categories.getJSONObject(j);
						
						if (!currCat.getString("id").equals(categoryId)){
							newCategories.put(currCat);
						}
					}
					
					currTask.put("categories", newCategories);
				}
				
				
				Utils.setTasksInProvider(result, mContext);
			}
			
			
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		
		return true;
	}

}
