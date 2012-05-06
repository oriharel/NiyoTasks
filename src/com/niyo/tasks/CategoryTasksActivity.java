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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.StringUtils;
import com.niyo.Utils;
import com.niyo.auto.AutoActivity;
import com.niyo.data.DBJsonFetchTask;
import com.niyo.data.NiyoContentProvider;
import com.niyo.tasks.map.AdGenericTaskActivity;

public class CategoryTasksActivity extends NiyoAbstractActivity {
	
	private static final String LOG_TAG = CategoryTasksActivity.class.getSimpleName();
	private static final int EDIT_TASK_CONTEXT_MENU_ITEM = 0;
	private static final int DELETE_TASK_CONTEXT_MENU_ITEM = 1;
	private ContentObserver mObserver;
	private TasksListAdapter mAdapter;
	private List<JSONObject> mOpenTasks;
	private List<JSONObject> mCrossedTasks;
	private Uri mUri;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_list_layout);
        getListView().setOnItemClickListener(getOnOpenTasksItemClicked());
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
        setOpenTasks(new ArrayList<JSONObject>());
        setCrossedTasks(new ArrayList<JSONObject>());
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
				}
				else if (adapterView.getItemIdAtPosition(position) == TasksListAdapter.CROSS_TASK_TYPE){
					JSONObject task = (JSONObject)adapterView.getItemAtPosition(position);
					removeFromCrossTasks(task);
					getOpenTasks().add(task);
					
				}
				
				processTasksToProvider();
			}
			
		};
		return result;
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	if (v instanceof ListView) 
		{
			if (menuInfo instanceof AdapterView.AdapterContextMenuInfo)
			{
				ListView list = (ListView) v;
				TasksListAdapter adapter = (TasksListAdapter)list.getAdapter();
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
				Object item = adapter.getItem(info.position);
				if (item instanceof JSONObject)
				{
					JSONObject bean = (JSONObject)item;
					try {
						menu.setHeaderTitle(bean.getString("content"));
					} catch (JSONException e) {
						ClientLog.e(LOG_TAG, "Error!", e);
					}
				}
			}
		}
    	menu.add(0, EDIT_TASK_CONTEXT_MENU_ITEM, 0, "Edit Item");
        menu.add(1, DELETE_TASK_CONTEXT_MENU_ITEM, 1, "Delete Item");
    }
	
	@Override
    public boolean onContextItemSelected(MenuItem item) 
	{
		AdapterView.AdapterContextMenuInfo info =
				(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		JSONObject task = null;
			
		ListView listView = getListView();
		TasksListAdapter adapter = (TasksListAdapter) listView.getAdapter();
		Object selectedItem = adapter.getItem(info.position);
		if (selectedItem instanceof JSONObject)
		{
			task = (JSONObject)selectedItem;
		}
		
		try {
			if (item.getItemId() == EDIT_TASK_CONTEXT_MENU_ITEM){

				Intent intent = null;

				intent = AdGenericTaskActivity.getCreationIntent(this, task.getString("id"));

				startActivity(intent);
				return true;
			}
			else if (item.getItemId() == DELETE_TASK_CONTEXT_MENU_ITEM){

				DeleteTaskTask deleteTask = new DeleteTaskTask(this, null);
				String[] params = new String[1];
				params[0] = task.getString("id");
				deleteTask.execute(params);
				return true;
			}
			else{
				return false;
			}

		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
			return false;
		}
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) 
	{
		ClientLog.d(LOG_TAG, "onCreateOptionsMenu started");
		try {
			MenuItem settingsMenuItem = menu.add(0, 0, 0, "Go!");
			settingsMenuItem.setIcon(R.drawable.ic_menu_directions);
		} catch (Exception e) 
		{
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem menuItem) 
	{
    	Intent intent = new Intent(this, AutoActivity.class);
    	startActivity(intent);
    	return true;
	}

	protected void deleteAllCrossed() {
		
		DeleteTasksFromCategory task = new DeleteTasksFromCategory(this);
		String[] params = new String[1];
		task.execute(params);
		
	}

	protected void removeFromOpenTasks(JSONObject task) {
		List<JSONObject> openTasks = new ArrayList<JSONObject>(getOpenTasks());
		
		for (JSONObject jsonObject : openTasks) {
			if (task.toString().equals(jsonObject.toString())){
				getOpenTasks().remove(jsonObject);
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
		
		Intent intent = AdGenericTaskActivity.getCreationIntent(this, null);
		startActivity(intent);
	}
	
	public static Intent getCreationIntent(Activity activity){
		
		Intent intent = new Intent(activity, CategoryTasksActivity.class);
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
					
					try {
						setOpenTasks(StringUtils.toList(extractTasks(tasks, false)));

						setCrossedTasks(StringUtils.toList(extractTasks(tasks, true)));
					} catch (JSONException e) {
						ClientLog.e(LOG_TAG, "Error!", e);
					}
				}
				else{
					ClientLog.d(LOG_TAG, "tasks list is empty in activity");
				}
				
				populateTasks();
			}

			@Override
			public void failure(Object data, String description) {
				ClientLog.e(LOG_TAG, "Error in service caller");

			}
		};

		DBJsonFetchTask task = new DBJsonFetchTask(this, caller);
		task.execute(getUri());
	}
	
	private JSONArray extractTasks(JSONArray tasks, Boolean done) throws JSONException {
		
		JSONArray result = new JSONArray();
		for (int i = 0; i < tasks.length(); i++){
			
			JSONObject task = tasks.getJSONObject(i);
			if (task.getBoolean("done") == done){
				result.put(task);
			}
		}
		return result;
	}
	
	
	private void processTasksToProvider() {
		
//		ProcessTasksTask task = new ProcessTasksTask(this, getIntent().getStringExtra(CATEGORY_EXTRA), getCrossedTasks(), getOpenTasks());
		
//		task.execute(new String[1]);
	}

	private void populateTasks() {
		
		List<JSONObject> openTasks = StringUtils.sort(getOpenTasks(), Utils.getTaskJSONComparator());
		List<JSONObject> crossedTasks = StringUtils.sort(getCrossedTasks(), Utils.getTaskJSONComparator());
		
		ClientLog.d(LOG_TAG, "num of open are: "+openTasks.size()+" num of crossed: "+crossedTasks.size());
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
