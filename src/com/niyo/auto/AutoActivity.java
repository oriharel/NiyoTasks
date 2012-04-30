package com.niyo.auto;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.auto.map.AutoMapActivity;
import com.niyo.data.DBJsonFetchTask;
import com.niyo.data.NiyoContentProvider;

public class AutoActivity extends NiyoAbstractActivity {
    /** Called when the activity is first created. */
	
	private static final String LOG_TAG = AutoActivity.class.getSimpleName();
	private Uri mUri;
	private JSONArray mTasks;
	private Boolean mTasksReady = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.where_to_layout);
        Log.d(LOG_TAG, "onCreate started");
        
        
        URL url= null;
        
		try {
			url = new URL("http://niyoapi.appspot.com/tasks");
		} catch (MalformedURLException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
			return;
		}
		
		
		Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+url.getPath());
		setUri(uri);
        getCategories();
        
    }
    
    private void getCategories() {
		
		ServiceCaller caller = new ServiceCaller() {
			
			@Override
			public void success(Object data) {
				
				if (data != null){
					JSONObject categoriesData = (JSONObject)data;
					try {
						setTasks(categoriesData.getJSONArray("tasks"));
					} catch (JSONException e) {
						ClientLog.e(LOG_TAG, "Error!", e);
					}
					
				}
				else{
					ClientLog.d(LOG_TAG, "categories list is empty in activity");
					setTasks(new JSONArray());
				}
				
			}
			
			@Override
			public void failure(Object data, String description) {
				ClientLog.e(LOG_TAG, "Error in service caller");
				
			}
		};
		
		DBJsonFetchTask task = new DBJsonFetchTask(this, caller);
		task.execute(getUri());
	}
    
    public void navigateTo(View view) 
	{
    	if (getTasksReady()){
    		String locationStr = (String)view.getTag();
        	String[] coordinsatesStrArray = locationStr.split(",");
        	AutoPoint to = new AutoPoint(Double.parseDouble(coordinsatesStrArray[0]), Double.parseDouble(coordinsatesStrArray[1]));
        	Intent intent;
			try {
				intent = AutoMapActivity.getCreationIntent(this, to, getCategoryIds());
				startActivity(intent);
			} catch (JSONException e) {
				ClientLog.e(LOG_TAG, "Error!", e);
			}
        	
    	}
    	
	}
    
    
	private ArrayList<String> getCategoryIds() throws JSONException {
		
		ArrayList<String> result = new ArrayList<String>();
		JSONArray tasks = getTasks();
		for (int i = 0; i < tasks.length(); i++){
			result.add(tasks.getJSONObject(i).getString("id"));
		}
		
		return result;
	}

	public Uri getUri() {
		return mUri;
	}

	public void setUri(Uri uri) {
		mUri = uri;
	}

	public JSONArray getTasks() {
		return mTasks;
	}

	public void setTasks(JSONArray tasks) {
		mTasks = tasks;
		setTasksReady(true);
		Toast.makeText(this, "tasks are ready", Toast.LENGTH_LONG).show();
	}

	public Boolean getTasksReady() {
		return mTasksReady;
	}

	public void setTasksReady(Boolean tasksReady) {
		mTasksReady = tasksReady;
	}
}