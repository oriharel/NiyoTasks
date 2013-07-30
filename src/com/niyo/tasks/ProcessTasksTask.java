//package com.niyo.tasks;
//
//import java.util.List;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.niyo.ClientLog;
//import com.niyo.data.JSONTableColumns;
//import com.niyo.data.NiyoContentProvider;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.AsyncTask;
//
//public class ProcessTasksTask extends AsyncTask<String, Void, Boolean> {
//
//	private static final String LOG_TAG = ProcessTasksTask.class.getSimpleName();
//	private Context mContext;
//	private String[] mProjection = new String[] 
//            {
//				JSONTableColumns._ID,
//				JSONTableColumns.ELEMENT_URL, 
//				JSONTableColumns.ELEMENT_JSON, 
//            };
//	private String mSelection = JSONTableColumns.ELEMENT_URL + "='/tasks'";
//	private String mCategory;
//	private List<JSONObject> mFinishedTasks;
//	private List<JSONObject> mUnfinishedTasks;
//	
//	public ProcessTasksTask(Context context, String category, List<JSONObject> finishedTasks, List<JSONObject> unfinishedTasks){
//		
//		mContext = context;
//		mCategory = category;
//		mFinishedTasks = finishedTasks;
//		mUnfinishedTasks = unfinishedTasks;
//	}
//	
//	@Override
//	protected Boolean doInBackground(String... params) {
//		
//		Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+"/tasks");
//		Cursor cursor = mContext.getContentResolver().query(uri, mProjection, mSelection, null, null);
//		JSONObject result = null;
//
//		if (cursor != null)
//		{
//			if (cursor.moveToFirst())
//			{
//				String jsonStr = cursor.getString(JSONTableColumns.COLUMN_JSON_INDEX);
//				ClientLog.d(LOG_TAG, "received "+jsonStr);
//				try {
//					result = new JSONObject(jsonStr);
//				} catch (JSONException e) {
//					ClientLog.e(LOG_TAG, "Error!", e);
//				}
//			}
//
//			cursor.close();
//		}
//		
//		JSONArray tasks;
//		try {
//			tasks = result.getJSONArray("tasks");
//
//			JSONObject categoryJson = null;
//
//			for (int i = 0; i < tasks.length(); i++){
//
//				if (tasks.getJSONObject(i).getString("category").equals(mCategory)){
//					categoryJson = tasks.getJSONObject(i);
//					break;
//				}
//			}
//
//			JSONArray categoryTasks = categoryJson.getJSONArray("tasks");
//			
//			for (int i = 0; i < categoryTasks.length(); i++){
//				
//				JSONObject savedTask = categoryTasks.getJSONObject(i);
//				
//				for (JSONObject finishedTask : mFinishedTasks) {
//					
//					if (finishedTask.getString("content").equals(savedTask.getString("content"))){
//						savedTask.put("done", true);
//					}
//				}
//				
//				for (JSONObject unFinishedTask : mUnfinishedTasks){
//					
//					if (unFinishedTask.getString("content").equals(savedTask.getString("content"))){
//						savedTask.put("done", false);
//					}
//				}
//			}
//
//			ContentValues values = new ContentValues();
//			values.put(JSONTableColumns.ELEMENT_URL, "/tasks");
//			values.put(JSONTableColumns.ELEMENT_JSON, result.toString());
//
//			mContext.getContentResolver().insert(Uri.parse(NiyoContentProvider.AUTHORITY+"/tasks"), values);
//			return true;
//		} catch (JSONException e) {
//			ClientLog.e(LOG_TAG, "Error!", e);
//			return false;
//		}
//	}
//
//}
