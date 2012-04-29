package com.niyo.categories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.niyo.ClientLog;
import com.niyo.data.JSONTableColumns;
import com.niyo.data.NiyoContentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

public class DeleteCategoryTask extends AsyncTask<String, Void, Boolean> {

	private static final String LOG_TAG = DeleteCategoryTask.class.getSimpleName();
	private Context mContext;
	private String[] mProjection = new String[] 
            {
				JSONTableColumns._ID,
				JSONTableColumns.ELEMENT_URL, 
				JSONTableColumns.ELEMENT_JSON, 
            };
	private String mSelection = JSONTableColumns.ELEMENT_URL + "='/tasks'";
	
	public DeleteCategoryTask(Context context){
		
		mContext = context;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {

		String category = params[0];
		
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
			JSONArray newTasks = new JSONArray();

			for (int i = 0; i < oldTasks.length(); i++){

				if (!oldTasks.getJSONObject(i).getString("category").equals(category)){
					newTasks.put(oldTasks.getJSONObject(i));
				}
			}
			
			result = new JSONObject("{tasks:"+newTasks+"}");

			ContentValues values = new ContentValues();
			values.put(JSONTableColumns.ELEMENT_URL, "/tasks");
			values.put(JSONTableColumns.ELEMENT_JSON, result.toString());

			mContext.getContentResolver().insert(Uri.parse(NiyoContentProvider.AUTHORITY+"/tasks"), values);
			return true;
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
			return false;
		}
	}

}
