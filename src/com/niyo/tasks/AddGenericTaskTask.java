package com.niyo.tasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.niyo.ClientLog;
import com.niyo.ServiceCaller;
import com.niyo.data.JSONTableColumns;
import com.niyo.data.NiyoContentProvider;

public class AddGenericTaskTask extends AsyncTask<LocationTask, Void, Boolean> {

	private static final String LOG_TAG = AddGenericTaskTask.class.getSimpleName();
	private Context mContext;
	private String[] mProjection = new String[] 
            {
				JSONTableColumns._ID,
				JSONTableColumns.ELEMENT_URL, 
				JSONTableColumns.ELEMENT_JSON, 
            };
	private String mSelection = JSONTableColumns.ELEMENT_URL + "='/locationTasks'";
	private ServiceCaller mCaller;
	
	public AddGenericTaskTask(Context context, ServiceCaller caller){
		
		mContext = context;
		mCaller = caller;
	}
	
	@Override
	protected Boolean doInBackground(LocationTask... params) {
		
		Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+"/locationTasks");
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
		try {

			JSONArray tasks = null;
			if (result == null){
				
				tasks = new JSONArray();
				result = new JSONObject("{tasks:"+tasks+"}");
			}
			else{
				tasks = result.getJSONArray("tasks");
			}
			
			LocationTask taskLocation = params[0];
			JSONObject taskJson = new JSONObject("{content:"+taskLocation.getTitle()+",lot:"+taskLocation.getDoublelat()+
					",lon:"+taskLocation.getDoubleLon()+"}");
			tasks.put(taskJson);
			
			ContentValues values = new ContentValues();
			values.put(JSONTableColumns.ELEMENT_URL, "/locationTasks");
			values.put(JSONTableColumns.ELEMENT_JSON, result.toString());

			mContext.getContentResolver().insert(Uri.parse(NiyoContentProvider.AUTHORITY+"/locationTasks"), values);
			
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
