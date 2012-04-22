package com.niyo.tasks;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.TextView;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.StringUtils;
import com.niyo.categories.CategoriesListAdapter;
import com.niyo.data.DBJsonFetchTask;
import com.niyo.data.NiyoContentProvider;

public class TasksActivity extends NiyoAbstractActivity {
	
	private static final String LOG_TAG = TasksActivity.class.getSimpleName();
	private ContentObserver mObserver;
	private TasksListAdapter mAdapter;
	private CrossedTasksListAdapter mCrossedAdapter;
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
        getCrossedListView().setOnItemClickListener(getOnCrossedTasksItemClicked());
        setCrossedTasks(new ArrayList<JSONObject>());
        registerForContextMenu(getListView());
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
	
	private OnItemClickListener getOnCrossedTasksItemClicked() {
		// TODO Auto-generated method stub
		return null;
	}

	private OnItemClickListener getOnOpenTasksItemClicked() {
		
		OnItemClickListener result = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
				
				if (adapterView.getItemIdAtPosition(position) == CategoriesListAdapter.ADD_CATEGORY_ITEM_ID){
					addTask(null);
				}
				else
				{
					JSONObject task = (JSONObject)adapterView.getSelectedItem();
					getOpenTasks().remove(task);
					getCrossedTasks().add(task);
					populateDoneTasks();
					populateOpenTasks();
				}
			}
			
		};
		return result;
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
					
					populateDoneTasks();
					populateOpenTasks();
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

		for (int i = 0; i < tasks.length(); i ++){
			try {
				if (tasks.getJSONObject(i).isNull("done") || !tasks.getJSONObject(i).getBoolean("done")){
						result.put(tasks.get(i));
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

		for (int i = 0; i < tasks.length(); i ++){
			try {
				ClientLog.d(LOG_TAG, "in extractDoneTasks curr task is "+tasks.getJSONObject(i));	
				if (!tasks.getJSONObject(i).isNull("done")){
					if (tasks.getJSONObject(i).getBoolean("done")){
						ClientLog.d(LOG_TAG, "putting done!");
						result.put(tasks.get(i));
					}
				}

			} catch (JSONException e) {
				ClientLog.e(LOG_TAG, "Error!", e);
			}
		}

		return result;
	}

	private void populateDoneTasks() {
		
		String category = getIntent().getStringExtra(CATEGORY_EXTRA);
		JSONArray categoryTasks = getCategoryTasks(getCrossedTasks(), category);
		
		if (getCrossedAdapter() != null){
			getCrossedAdapter().setList(categoryTasks);
			getCrossedAdapter().notifyDataSetChanged();
		}
		else{
			setCrossedAdapter(new CrossedTasksListAdapter(this));
			getCrossedAdapter().setList(categoryTasks);
			getCrossedListView().setAdapter(getCrossedAdapter());
		}
	}
	
	
	private void populateOpenTasks() {
		
		String category = getIntent().getStringExtra(CATEGORY_EXTRA);
		JSONArray categoryTasks = getCategoryTasks(getOpenTasks(), category);
		if (getAdapter() != null){
			getAdapter().setList(categoryTasks);
			getAdapter().notifyDataSetChanged();
		}
		else{
			setAdapter(new TasksListAdapter(this));
			getAdapter().setList(categoryTasks);
			getListView().setAdapter(getAdapter());
		}
	}

	private JSONArray getCategoryTasks(List<JSONObject> tasks, String category) {

		JSONArray result = new JSONArray();
		ClientLog.d(LOG_TAG, "tasks is "+StringUtils.printList(tasks));
		for (JSONObject task : tasks) {
			
			ClientLog.d(LOG_TAG, "task is "+task);
			try {
				if (task.get("category").equals(category)){
					result = task.getJSONArray("tasks");
					return result;
				}

			} catch (JSONException e) {
				ClientLog.e(LOG_TAG, "Error!", e);
			}
		}

		return result;
	}

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
	
	private ListView getCrossedListView(){
		return (ListView)findViewById(R.id.crossedOffList);
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

	public CrossedTasksListAdapter getCrossedAdapter() {
		return mCrossedAdapter;
	}

	public void setCrossedAdapter(CrossedTasksListAdapter crossedAdapter) {
		mCrossedAdapter = crossedAdapter;
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
