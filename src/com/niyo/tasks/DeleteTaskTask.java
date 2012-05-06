package com.niyo.tasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.niyo.ClientLog;
import com.niyo.ServiceCaller;
import com.niyo.Utils;

public class DeleteTaskTask extends AsyncTask<String, Void, Boolean> {

	private static final String LOG_TAG = DeleteTaskTask.class.getSimpleName();
	private Context mContext;
	
	private ServiceCaller mCaller;
	
	public DeleteTaskTask(Context context, ServiceCaller caller){
		
		mContext = context;
		mCaller = caller;
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
				
				JSONArray newTasksArray = new JSONArray();
				
				String taskId = params[0];
				
				for (int i = 0; i < tasks.length(); i++){
					
					JSONObject currTask = tasks.getJSONObject(i);
					
					if (!currTask.getString("id").equals(taskId)){
						newTasksArray.put(currTask);
					}
				}
				
				result.put("tasks", newTasksArray);
				
				Utils.setTasksInProvider(result, mContext);
			}
			
			
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		
		return true;
	}
	
	@Override
    protected void onPostExecute(Boolean result) 
	{
        if (isCancelled()) 
        {
        	ClientLog.d(LOG_TAG, "isCancelled activated");
        }
        
        ClientLog.d(LOG_TAG, "calling success");
        try 
        {
        	if (mCaller != null){
        		mCaller.success(result);
        	}
			
		} 
        catch (Exception e) 
        {
			ClientLog.e(LOG_TAG, "Error! with ", e);
		}
    }

}
