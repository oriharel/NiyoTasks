//package com.niyo.data;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.AsyncTask;
//
//import com.niyo.ClientLog;
//import com.niyo.ServiceCaller;
//
//public class DBJsonFetchTask extends AsyncTask<Uri, Void, JSONObject> {
//
//	private static final String LOG_TAG = DBJsonFetchTask.class.getSimpleName();
//	private Context mContext;
//	private ServiceCaller mCaller;
//	private String[] _projection = new String[] 
//            {
//				JSONTableColumns._ID,
//				JSONTableColumns.ELEMENT_URL, 
//				JSONTableColumns.ELEMENT_JSON, 
//            };
//	
//	public DBJsonFetchTask(Context context, ServiceCaller caller)
//	{
//		setContext(context);
//		setCaller(caller);
//	}
//	
//	@Override
//	protected JSONObject doInBackground(Uri... params) {
//		
//		Uri uri = params[0];
//		String selection = JSONTableColumns.ELEMENT_URL + "='/" +uri.getLastPathSegment()+ "'";
//		ClientLog.d(LOG_TAG, "selecting "+selection);
//		
//		Cursor cursor = getContext().getContentResolver().query(uri, _projection, selection, null, null);
//		JSONObject result = null;
//		
//		if (cursor != null)
//		{
//			if (cursor.moveToFirst())
//			{
//				String jsonStr = cursor.getString(JSONTableColumns.COLUMN_JSON_INDEX);
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
//		
//		return result;
//		
//	}
//	
//	@Override
//    protected void onPostExecute(JSONObject data) 
//	{
//        if (isCancelled()) 
//        {
//        	ClientLog.d(LOG_TAG, "isCancelled activated");
//        }
//        
//        ClientLog.d(LOG_TAG, "calling success");
//        try 
//        {
//			getCaller().success(data);
//		} 
//        catch (Exception e) 
//        {
//			ClientLog.e(LOG_TAG, "Error! with ", e);
//		}
//    }
//	
//	
//	public Context getContext() {
//		return mContext;
//	}
//	public void setContext(Context context) {
//		mContext = context;
//	}
//
//	public ServiceCaller getCaller() {
//		return mCaller;
//	}
//
//	public void setCaller(ServiceCaller caller) {
//		mCaller = caller;
//	}
//
//}
