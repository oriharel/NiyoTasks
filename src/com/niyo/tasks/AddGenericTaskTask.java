//package com.niyo.tasks;
//
//import java.util.List;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Context;
//import android.os.AsyncTask;
//
//import com.niyo.ClientLog;
//import com.niyo.ServiceCaller;
//import com.niyo.Utils;
//import com.niyo.categories.CategoryBean;
//
//public class AddGenericTaskTask extends AsyncTask<LocationTask, Void, Boolean> {
//
//	private static final String LOG_TAG = AddGenericTaskTask.class.getSimpleName();
//	private Context mContext;
//	
//	private ServiceCaller mCaller;
//	
//	public AddGenericTaskTask(Context context, ServiceCaller caller){
//		
//		mContext = context;
//		mCaller = caller;
//	}
//	
//	@Override
//	protected Boolean doInBackground(LocationTask... params) {
//		
//		JSONObject result = Utils.getTasksFromProvider(mContext);
//
//		try {
//
//			JSONArray tasks = null;
//			if (result == null){
//				
//				result = new JSONObject("{tasks:"+new JSONArray()+"}");
//			}
//			
//			tasks = result.getJSONArray("tasks");
//			
//			LocationTask taskLocation = params[0];
//			JSONObject taskJson = new JSONObject("{id:\""+taskLocation.getId()+"\",content:\""+taskLocation.getTitle()+"\",lat:\""+taskLocation.getDoublelat()+
//					"\",lon:\""+taskLocation.getDoubleLon()+"\",done:"+Boolean.FALSE+",userInputAddress:\""+taskLocation.getUserInputAddress()+
//					"\",categories:"+createCategoriesArray(taskLocation)+"}");
//			
//			int index = getTaskIndex(tasks, taskLocation);
//			
//			ClientLog.d(LOG_TAG, "got index "+index);
//			
//			if (index >= 0){
//				tasks.put(index, taskJson);
//			}
//			else{
//				tasks.put(taskJson);
//			}
//			
//			
//			Utils.setTasksInProvider(result, mContext);
//			Utils.setupProximityAlerts(mContext);
//			
//		} catch (JSONException e) {
//			ClientLog.e(LOG_TAG, "Error!", e);
//		}
//		
//		return true;
//	}
//	
//	private String createCategoriesArray(LocationTask taskLocation) throws JSONException {
//		
//		List<CategoryBean> categories = taskLocation.getCategories();
//		JSONArray result = new JSONArray();
//		for (CategoryBean categoryBean : categories) {
//			JSONObject category = new JSONObject("{name:\""+categoryBean.getName()+"\",id:\""+categoryBean.getId()+"\"}");
//			result.put(category);
//		}
//		return result.toString();
//	}
//
//	private int getTaskIndex(JSONArray tasks, LocationTask taskLocation) throws JSONException {
//		
//		int result = -1;
//		for (int i = 0; i < tasks.length(); i++){
//			JSONObject task = tasks.getJSONObject(i);
//			ClientLog.d(LOG_TAG, "equilizing "+task.getString("id")+" with "+taskLocation.getId());
//			if (task.getString("id").equals(taskLocation.getId())){
//				result = i;
//				return result;
//			}
//		}
//		
//		return result;
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
//        	if (mCaller != null){
//        		mCaller.success(result);
//        	}
//			
//		} 
//        catch (Exception e) 
//        {
//			ClientLog.e(LOG_TAG, "Error! with ", e);
//		}
//    }
//
//}
