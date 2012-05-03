package com.niyo.tasks;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.niyo.auto.AutoActivity;
import com.niyo.categories.CategoriesListAdapter;
import com.niyo.categories.DeleteCategoryTask;
import com.niyo.data.DBJsonFetchTask;
import com.niyo.data.DeleteHttpTask;
import com.niyo.data.NiyoContentProvider;

public class TasksActivity extends NiyoAbstractActivity implements OnItemClickListener {
	
	private static final String LOG_TAG = TasksActivity.class.getSimpleName();
	
	private CategoriesListAdapter mAdapter;
	private ContentObserver mObserver;
	private static final int DELETE_CATEGORY_CONTEXT_MENU_ITEM = 1; 
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
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
				getCategories();
			}
		};
		
		Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+url.getPath());
        getContentResolver().registerContentObserver(uri, false, mObserver);
        getCategories();
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	if (v instanceof ListView) 
		{
			if (menuInfo instanceof AdapterView.AdapterContextMenuInfo)
			{
				ListView list = (ListView) v;
				CategoriesListAdapter adapter = (CategoriesListAdapter)list.getAdapter();
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
				Object item = adapter.getItem(info.position);
				if (item instanceof JSONObject)
				{
					JSONObject bean = (JSONObject)item;
					try {
						menu.setHeaderTitle(bean.getString("category"));
					} catch (JSONException e) {
						ClientLog.e(LOG_TAG, "Error!", e);
					}
				}
			}
		}
        menu.add(0, DELETE_CATEGORY_CONTEXT_MENU_ITEM, 0, "Delete Category");
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) 
	{
		AdapterView.AdapterContextMenuInfo info =
			(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		
		ListView listView = getList();
		CategoriesListAdapter adapter = (CategoriesListAdapter) listView.getAdapter();
		Object selectedItem = adapter.getItem(info.position);
		if (selectedItem instanceof JSONObject)
		{
			JSONObject bean = (JSONObject)selectedItem;
			DeleteHttpTask task = new DeleteHttpTask(this);
			try {
				
				String paramValue = bean.getString("category");
				String encoded = URLEncoder.encode(paramValue.toString(), "UTF-8");
				task.execute(new URL("http://niyoapi.appspot.com/deleteCategory?name="+encoded));
				
				DeleteCategoryTask deleteTask = new DeleteCategoryTask(this);
				String[] params = new String[1];
				params[0] = bean.getString("category");
				deleteTask.execute(params);
			} catch (Exception e) {
				ClientLog.e(LOG_TAG, "Errro!", e);
			}
		}
		
		return false;
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

	private void getCategories() {
		
		ServiceCaller caller = new ServiceCaller() {
			
			@Override
			public void success(Object data) {
				
				JSONArray tasks = null;
				if (data != null){
					JSONObject categoriesData = (JSONObject)data;
					try {
						tasks = categoriesData.getJSONArray("tasks");
					} catch (JSONException e) {
						ClientLog.e(LOG_TAG, "Error!", e);
					}
					
				}
				else{
					ClientLog.d(LOG_TAG, "categories list is empty in activity");
					tasks = new JSONArray();
				}
				
				setTasks(tasks);
			}
			
			@Override
			public void failure(Object data, String description) {
				ClientLog.e(LOG_TAG, "Error in service caller");
				
			}
		};
		
		DBJsonFetchTask task = new DBJsonFetchTask(this, caller);
		
		URL url= null;

		try {
			url = new URL("http://niyoapi.appspot.com/tasks");
		} catch (MalformedURLException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
			return;
		}
		Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+url.getPath());
		task.execute(uri);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		getContentResolver().unregisterContentObserver(mObserver);
	}

	private void setTasks(JSONArray tasks) {
//		ClientLog.d(LOG_TAG, "setTasks started with "+tasks);
//		if (getAdapter() != null)
//		{
//			getAdapter().setList(tasks);
//			getAdapter().notifyDataSetChanged();
//		}
//		else
//		{
//			setAdapter(new CategoriesListAdapter(this));
//			getAdapter().setList(tasks);
//			getList().setAdapter(getAdapter());
//		}
	}
	
	private ListView getList() {
		return null;
	}

	public CategoriesListAdapter getAdapter() {
		return mAdapter;
	}

	public void setAdapter(CategoriesListAdapter adapter) {
		mAdapter = adapter;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
		
		
//		if (adapterView.getItemIdAtPosition(position) == CategoriesListAdapter.ADD_CATEGORY_ITEM_ID){
//			Intent intent = AddCategoryActivity.getCreationIntent(this);
//			startActivity(intent);
//		}
//		else if (adapterView.getItemIdAtPosition(position) == CategoriesListAdapter.ADD_GENERIC_ITEM_ID){
//			Intent intent = AdGenericTaskActivity.getCreationIntent(this);
//			startActivity(intent);
//		}
//		else
//		{
//			JSONObject json = (JSONObject) adapterView.getItemAtPosition(position);
//			
//			try {
//				Intent intent = CategoryTasksActivity.getCreationIntent(this, json.getString("category"));
//				startActivity(intent);
//			} catch (JSONException e) {
//				ClientLog.e(LOG_TAG, "Error!", e);
//			}
//		}
		
	}
}