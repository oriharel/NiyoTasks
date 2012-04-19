package com.niyo.tasks;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.data.PostJsonTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AddTaskActivity extends NiyoAbstractActivity {
	
	private static final String LOG_TAG = AddTaskActivity.class.getSimpleName();
	private static final String CATEGORY_EXTRA = "category";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task_layout);
	}
	
	public static Intent getCreationIntent(Activity activity, String category){
		
		Intent result = new Intent(activity, AddTaskActivity.class);
		result.putExtra(CATEGORY_EXTRA, category);
		return result;
	}
	
	public void addTask(View v){
		
		TextView taskName = (TextView)findViewById(R.id.taskNameEdit);
		JSONObject jsonObj = new JSONObject();
		
		try {
			jsonObj.put("category", getIntent().getStringExtra(CATEGORY_EXTRA));
			jsonObj.put("content", taskName.getText());
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		
		HttpEntity entity = null;
		
		try {
			entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
			PostJsonTask task = new PostJsonTask(entity, this, getServiceCaller());
			try {
				URL url = new URL("http://niyoapi.appspot.com/add");
				task.execute(url);
			} catch (Exception e) {
				ClientLog.e(LOG_TAG, "Error!", e);
			}
			
		} catch (Exception e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
	}

	private ServiceCaller getServiceCaller() {

		ServiceCaller result = new ServiceCaller() {
			
			@Override
			public void success(Object data) {
				setResult(RESULT_OK);
				finish();
				
			}
			
			@Override
			public void failure(Object data, String description) {
				Toast.makeText(AddTaskActivity.this, "Error, something went wrong", Toast.LENGTH_LONG).show();
				
			}
		};
		
		return result;
	}
	

}
