package com.niyo.tasks;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.data.DBJsonFetchTask;
import com.niyo.data.NiyoContentProvider;

public class TasksActivity extends NiyoAbstractActivity implements OnItemClickListener {
	
	private static final String LOG_TAG = TasksActivity.class.getSimpleName();
	private ContentObserver mObserver;
	private TasksListAdapter mAdapter;
	private Uri mUri;
	private static final String CATEGORY_EXTRA = "category";
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getList().setOnItemClickListener(this);
        registerForContextMenu(getList());
        URL url= null;
        
		try {
			url = new URL("http://niyoapi.appspot.com/tasks");
		} catch (MalformedURLException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
			return;
		}
		
        mObserver = new ContentObserver(getHandler()) {
        	public void onChange(boolean selfChange) 
			{
				ClientLog.d(LOG_TAG, "onChange started");
				getTasks();
			}
		};
		
		Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+url.getPath());
		setUri(uri);
        getContentResolver().registerContentObserver(uri, false, mObserver);
        getContentResolver().update(uri, null, null, null);
        getTasks();
	}
	
	public static Intent getCreationIntent(Activity activity, String category){
		
		Intent intent = new Intent(activity, TasksActivity.class);
		intent.putExtra(CATEGORY_EXTRA, category);
		return intent;
	}
	
	protected void getTasks() {

		ServiceCaller caller = new ServiceCaller() {

			@Override
			public void success(Object data) {
				
				if (data != null){
					JSONObject tasksData = (JSONObject)data;
					JSONArray tasks = null;
					try {
						tasks = tasksData.getJSONArray("tasks");
					} catch (JSONException e) {
						ClientLog.e(LOG_TAG, "Error!", e);
					}
					setTasks(tasks);
				}
				else{
					ClientLog.d(LOG_TAG, "tasks list is empty in activity");
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
	
	private void setTasks(JSONArray tasks) {
		if (getAdapter() != null)
		{
			getAdapter().setList(tasks);
			getAdapter().notifyDataSetChanged();
		}
		else
		{
			setAdapter(new TasksListAdapter(this));
			getAdapter().setList(tasks);
			getList().setAdapter(getAdapter());
		}
	}

	private void setUri(Uri uri)
	{
		mUri = uri;
	}
	
	private Uri getUri()
	{
		return mUri;
	}

	
	private ListView getList() {
		
		return (ListView)findViewById(R.id.categoriesList);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		getContentResolver().unregisterContentObserver(mObserver);
	}

	public TasksListAdapter getAdapter() {
		return mAdapter;
	}

	public void setAdapter(TasksListAdapter adapter) {
		mAdapter = adapter;
	}
}
