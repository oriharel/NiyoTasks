//package com.niyo.data;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.niyo.ClientLog;
//import com.niyo.ServiceCaller;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.AsyncTask;
//
//public class AddCategoryToProviderTask extends AsyncTask<String, Void, Boolean> {
//
//	private static final String LOG_TAG = AddCategoryToProviderTask.class.getSimpleName();
//	private Context mContext;
//	private String[] mProjection = new String[] 
//            {
//				JSONTableColumns._ID,
//				JSONTableColumns.ELEMENT_URL, 
//				JSONTableColumns.ELEMENT_JSON, 
//            };
//	private String mSelection = JSONTableColumns.ELEMENT_URL + "='/tasks'";
//	private ServiceCaller mCaller;
//	
//	public AddCategoryToProviderTask(Context context, ServiceCaller caller){
//		
//		mContext = context;
//		mCaller = caller;
//	}
//	
//	@Override
//	protected Boolean doInBackground(String... params) {
//
//		String category = params[0];
//		String id = params[1];
//		
//		ClientLog.d(LOG_TAG, "inserting category:"+category);
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
//		try {
//		
//			if (result == null){
//				result = new JSONObject("{tasks:"+new JSONArray()+"}");
//			}
//	
//			JSONArray oldTasks;
//		
//			oldTasks = result.getJSONArray("tasks");
//
//
//			oldTasks.put(createNewJsonObject(category, id));
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
//	private JSONObject createNewJsonObject(String category, String id) throws JSONException {
//		
//		return new JSONObject("{category:\""+category+"\",tasks:"+new JSONArray()+", id:"+id+"}");
//		
//	}
//	
//	@Override
//    protected void onPostExecute(Boolean result) 
//	{
//        if (isCancelled()) 
//        {
//        	ClientLog.d(LOG_TAG, "isCancelled activated");
//        }
//        
//        ClientLog.d(LOG_TAG, "calling success");
//        try 
//        {
//			mCaller.success(result);
//		} 
//        catch (Exception e) 
//        {
//			ClientLog.e(LOG_TAG, "Error! with ", e);
//		}
//    }
//
//}
