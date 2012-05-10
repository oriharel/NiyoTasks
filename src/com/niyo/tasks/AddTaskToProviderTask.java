package com.niyo.tasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.niyo.ClientLog;
import com.niyo.ServiceCaller;
import com.niyo.Utils;
import com.niyo.data.JSONTableColumns;
import com.niyo.data.NiyoContentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

public class AddTaskToProviderTask extends AsyncTask<String, Void, Boolean> {

	private static final String LOG_TAG = AddTaskToProviderTask.class.getSimpleName();
	private Context mContext;
	private String[] mProjection = new String[] 
            {
				JSONTableColumns._ID,
				JSONTableColumns.ELEMENT_URL, 
				JSONTableColumns.ELEMENT_JSON, 
            };
	private String mSelection = JSONTableColumns.ELEMENT_URL + "='/tasks'";
	private ServiceCaller mCaller;
	
	public AddTaskToProviderTask(Context context, ServiceCaller caller){
		mContext = context;
		mCaller = caller;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {

		String content = params[0];
		String category = params[1];
		
		ClientLog.d(LOG_TAG, "inserting content: "+content+" category:"+category);

		Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+"/tasks");
		Cursor cursor = mContext.getContentResolver().query(uri, mProjection, mSelection, null, null);
		JSONObject result = null;

		if (cursor != null)
		{
			if (cursor.moveToFirst())
			{
				String jsonStr = cursor.getString(JSONTableColumns.COLUMN_JSON_INDEX);
				ClientLog.d(LOG_TAG, "received "+jsonStr);
				try {
					result = new JSONObject(jsonStr);
				} catch (JSONException e) {
					ClientLog.e(LOG_TAG, "Error!", e);
				}
			}

			cursor.close();
		}

		JSONArray oldTasks;
		try {
			oldTasks = result.getJSONArray("tasks");

			JSONObject oldCategoryJson = null;

			for (int i = 0; i < oldTasks.length(); i++){

				if (oldTasks.getJSONObject(i).getString("category").equals(category)){
					oldCategoryJson = oldTasks.getJSONObject(i);
				}
			}

			createNewJsonObject(oldCategoryJson, content);

			ContentValues values = new ContentValues();
			values.put(JSONTableColumns.ELEMENT_URL, "/tasks");
			values.put(JSONTableColumns.ELEMENT_JSON, result.toString());

			mContext.getContentResolver().insert(Uri.parse(NiyoContentProvider.AUTHORITY+"/tasks"), values);
			
			Utils.setupProximityAlerts(mContext);
			return true;
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
			return false;
		}
	}

	private JSONObject createNewJsonObject(JSONObject oldCategoryJson, String content) {

		ClientLog.d(LOG_TAG, "oldCategoryJson is: "+oldCategoryJson);
		try {
			JSONArray newArray = new JSONArray();

			JSONArray oldTasks;

			oldTasks = oldCategoryJson.getJSONArray("tasks");


//			for (int i = 0; i < oldTasks.length(); i++){
//				newArray.put(oldTasks.getJSONObject(i));
//			}

			oldTasks.put(new JSONObject("{content:\""+content+"\",category:\""+oldCategoryJson.getString("category")+"\", done:"+Boolean.FALSE+"}"));
			String newJsonStr = "{\"category\":\""+oldCategoryJson.getString("category")+"\",\"tasks\":"+newArray+"}";
			ClientLog.d(LOG_TAG, "the new category json is: "+newJsonStr);

			return new JSONObject(newJsonStr);
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
			return null;
		}
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
			mCaller.success(result);
		} 
        catch (Exception e) 
        {
			ClientLog.e(LOG_TAG, "Error! with ", e);
		}
    }

}
