package com.niyo.tasks;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.StringUtils;
import com.niyo.Utils;
import com.niyo.data.DBJsonFetchTask;
import com.niyo.data.NiyoContentProvider;
import com.niyo.data.PutJsonTask;

public class TasksActivity extends NiyoAbstractActivity {
	
	private static final String LOG_TAG = TasksActivity.class.getSimpleName();
	private ContentObserver mObserver;
	private TasksListAdapter mAdapter;
	private List<JSONObject> mOpenTasks;
	private List<JSONObject> mCrossedTasks;
	private Uri mUri;
	private static final String CATEGORY_EXTRA = "category";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_list_layout);
        TextView categoryTitle = (TextView)findViewById(R.id.categoryTitle);
        categoryTitle.setText(getIntent().getStringExtra(CATEGORY_EXTRA));
        getListView().setOnItemClickListener(getOnOpenTasksItemClicked());
        setCrossedTasks(new ArrayList<JSONObject>());
        registerForContextMenu(getListView());
        findViewById(R.id.addTask).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addTask(v);
			}
		});
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
        getTasks();
	}
	
	private OnItemClickListener getOnOpenTasksItemClicked() {
		
		OnItemClickListener result = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
				
				if (adapterView.getItemIdAtPosition(position) == TasksListAdapter.ADD_TASK_TYPE){
					addTask(null);
				}
				else if (adapterView.getItemIdAtPosition(position) == TasksListAdapter.DELETE_ALL_CROSSED_TYPE){
					deleteAllCrossed();
				}
				else if (adapterView.getItemIdAtPosition(position) == TasksListAdapter.OPEN_TASK_TYPE){
					
					JSONObject task = (JSONObject)adapterView.getItemAtPosition(position);
					removeFromOpenTasks(task);
					getCrossedTasks().add(task);
					populateTasks();
					finishTasksToServer();
				}
				else if (adapterView.getItemIdAtPosition(position) == TasksListAdapter.CROSS_TASK_TYPE){
					JSONObject task = (JSONObject)adapterView.getItemAtPosition(position);
					removeFromCrossTasks(task);
					getOpenTasks().add(task);
					populateTasks();
					finishTasksToServer();
					
				}
			}
			
		};
		return result;
	}

	protected void deleteAllCrossed() {
		
		String finishTaskIds = getCrossedTasksIds();
		JSONArray finishTaskIdsArray;
		JSONObject jsonObj = null;
		try {
			finishTaskIdsArray = new JSONArray(finishTaskIds);
			jsonObj = new JSONObject();

			jsonObj.put("taskIds", finishTaskIdsArray);
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}

		HttpEntity entity = null;

		try {
			entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
			PutJsonTask task = new PutJsonTask(entity, this, getServiceCaller());
			try {
				URL url = new URL("http://niyoapi.appspot.com/deleteTasksFromList");
				task.execute(url);
			} catch (Exception e) {
				ClientLog.e(LOG_TAG, "Error!", e);
			}

		} catch (Exception e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		
	}

	protected void removeFromOpenTasks(JSONObject task) {
		List<JSONObject> openTasks = new ArrayList<JSONObject>(getOpenTasks());
		
		for (JSONObject jsonObject : openTasks) {
			if (task.toString().equals(jsonObject.toString())){
				boolean isDelete = getOpenTasks().remove(jsonObject);
			}
		}
		
	}
	
	protected void removeFromCrossTasks(JSONObject task) {
		List<JSONObject> crossedTasks = new ArrayList<JSONObject>(getCrossedTasks());
		
		for (JSONObject jsonObject : crossedTasks) {
			if (task.toString().equals(jsonObject.toString())){
				boolean isDelete = getCrossedTasks().remove(jsonObject);
				ClientLog.d(LOG_TAG, "success remove? "+isDelete);
			}
		}
		
	}

	public void addTask(View v){
		Intent intent = AddTaskActivity.getCreationIntent(this, getIntent().getStringExtra(CATEGORY_EXTRA));
		startActivityForResult(intent, 0);
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
					
					setOpenTasks(StringUtils.toList(extractOpenTasks(tasks)));
					setCrossedTasks(StringUtils.toList(extractDoneTasks(tasks)));
					
					populateTasks();
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
	
	private JSONArray extractOpenTasks(JSONArray tasks) {

		JSONArray result = new JSONArray();
		String category = getIntent().getStringExtra(CATEGORY_EXTRA);
		
		JSONArray categoryTasks = getCategoryTasks(tasks, category);
		for (int i = 0; i < categoryTasks.length(); i ++){
			try {
				JSONObject currObj = categoryTasks.getJSONObject(i);

				if (currObj.isNull("done") || !currObj.getBoolean("done")){
					result.put(categoryTasks.get(i));
				}
			}
			catch (JSONException e) {
				ClientLog.w(LOG_TAG, "Error!", e);
			}
		}

		return result;
	}
	
	private JSONArray extractDoneTasks(JSONArray tasks) {

		JSONArray result = new JSONArray();
		String category = getIntent().getStringExtra(CATEGORY_EXTRA);
		
		JSONArray categoryTasks = getCategoryTasks(tasks, category);
		for (int i = 0; i < categoryTasks.length(); i ++){
			try {
				JSONObject currObj = categoryTasks.getJSONObject(i);

				if (!currObj.isNull("done") && currObj.getBoolean("done")){
					result.put(categoryTasks.get(i));
				}
			}
			catch (JSONException e) {
				ClientLog.w(LOG_TAG, "Error!", e);
			}
		}

		return result;
	}
	
	
	private JSONArray getCategoryTasks(JSONArray tasks, String category) {
		try {
			for (int i = 0; i < tasks.length(); i++){
				JSONObject currObj = tasks.getJSONObject(i);
				if (currObj.getString("category").equals(category)){
					return currObj.getJSONArray("tasks");
				}
			}
		}
		catch (JSONException e) {
			ClientLog.w(LOG_TAG, "Error!", e);
		}

		return null;
	}
	
	private void finishTasksToServer() {
		
		String finishTaskIds = getCrossedTasksIds();
		String unFinishTaskIds = getOpenTasksIds();
		JSONArray finishTaskIdsArray;
		JSONArray unFinishTaskIdsArray;
		JSONObject jsonObj = null;
		try {
			finishTaskIdsArray = new JSONArray(finishTaskIds);
			unFinishTaskIdsArray = new JSONArray(unFinishTaskIds);
			jsonObj = new JSONObject();

			jsonObj.put("finishTaskIds", finishTaskIdsArray);
			jsonObj.put("unFinishTaskIds", unFinishTaskIdsArray);
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}

		HttpEntity entity = null;

		try {
			entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
			PutJsonTask task = new PutJsonTask(entity, this, getServiceCaller());
			try {
				URL url = new URL("http://niyoapi.appspot.com/finishTasks");
				task.execute(url);
			} catch (Exception e) {
				ClientLog.e(LOG_TAG, "Error!", e);
			}

		} catch (Exception e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
	}

	private String getOpenTasksIds() {

		List<JSONObject> openTasks = getOpenTasks();
		
		StringBuffer result = new StringBuffer();
		result.append("[");
		
		for (int i = 0; i < openTasks.size(); i++) {
			try {
				result.append(openTasks.get(i).getString("taskId"));
			} catch (JSONException e) {
				ClientLog.e(LOG_TAG, "Error!", e);
			}
			
			if (i < openTasks.size()-1){
				result.append(",");
			}
		}
		
		result.append("]");
		
		ClientLog.d(LOG_TAG, "preparing finish tasks with "+result);
		
		return result.toString();
	}

	private String getCrossedTasksIds() {
		
		List<JSONObject> crossedTasks = getCrossedTasks();
		
		StringBuffer result = new StringBuffer();
		result.append("[");
		
		for (int i = 0; i < crossedTasks.size(); i++) {
			try {
				result.append(crossedTasks.get(i).getString("taskId"));
			} catch (JSONException e) {
				ClientLog.e(LOG_TAG, "Error!", e);
			}
			
			if (i < crossedTasks.size()-1){
				result.append(",");
			}
		}
		
		result.append("]");
		
		ClientLog.d(LOG_TAG, "preparing finish tasks with "+result);
		
		return result.toString();
	}

	private ServiceCaller getServiceCaller() {
		
		return new ServiceCaller() {
			
			@Override
			public void success(Object data) {
				ClientLog.d(LOG_TAG, "successfully finished tasks");
			}
			
			@Override
			public void failure(Object data, String description) {
				ClientLog.d(LOG_TAG, "failed to finish tasks");
			}
		};
	}

	private void populateTasks() {
		
		List<JSONObject> openTasks = StringUtils.sort(getOpenTasks(), Utils.getTaskJSONComparator());
		List<JSONObject> crossedTasks = StringUtils.sort(getCrossedTasks(), Utils.getTaskJSONComparator());
		if (getAdapter() != null){
			getAdapter().setList(openTasks, crossedTasks);
			getAdapter().notifyDataSetChanged();
		}
		else{
			setAdapter(new TasksListAdapter(this));
			getAdapter().setList(openTasks, crossedTasks);
			getListView().setAdapter(getAdapter());
		}
	}

//	private JSONArray getCategoryTasks(List<JSONObject> tasks, String category) {
//
//		JSONArray result = new JSONArray();
//		ClientLog.d(LOG_TAG, "tasks is "+StringUtils.printList(tasks));
//		for (JSONObject task : tasks) {
//			
//			ClientLog.d(LOG_TAG, "task is "+task);
//			try {
//				if (task.get("category").equals(category)){
//					result = task.getJSONArray("tasks");
//					return result;
//				}
//
//			} catch (JSONException e) {
//				ClientLog.e(LOG_TAG, "Error!", e);
//			}
//		}
//
//		return result;
//	}

	private void setUri(Uri uri)
	{
		mUri = uri;
	}
	
	private Uri getUri()
	{
		return mUri;
	}

	
	private ListView getListView() {
		
		return (ListView)findViewById(R.id.tasksList);
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


	public List<JSONObject> getCrossedTasks() {
		return mCrossedTasks;
	}

	public void setCrossedTasks(List<JSONObject> crossedTasks) {
		mCrossedTasks = crossedTasks;
	}

	public List<JSONObject> getOpenTasks() {
		return mOpenTasks;
	}

	public void setOpenTasks(List<JSONObject> openTasks) {
		mOpenTasks = openTasks;
	}
}
