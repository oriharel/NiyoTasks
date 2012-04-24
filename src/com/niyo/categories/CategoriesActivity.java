package com.niyo.categories;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.PeriodicSync;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.data.DBJsonFetchTask;
import com.niyo.data.DeleteHttpTask;
import com.niyo.data.JsonDbInsertTask;
import com.niyo.data.NiyoContentProvider;
import com.niyo.data.PostJsonTask;
import com.niyo.tasks.TasksActivity;

public class CategoriesActivity extends NiyoAbstractActivity implements OnItemClickListener {
	
	private static final String LOG_TAG = CategoriesActivity.class.getSimpleName();
	private static final int ADD_NEW_CATEGORY_DIALOG = 0;
	private Uri mUri;
	
	private CategoriesListAdapter mAdapter;
	private ContentObserver mObserver;
	private static final int DELETE_CATEGORY_CONTEXT_MENU_ITEM = 1; 
	
    /** Called when the activity is first created. */
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
				getCategories();
			}
		};
		
		Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+url.getPath());
		setUri(uri);
        getContentResolver().registerContentObserver(uri, false, mObserver);
        getContentResolver().update(uri, null, null, null);
        Account account = new Account("ori", "1234");
        Bundle extras = new Bundle();
        extras.putString("a", "1");
        int period = 1000;
        PeriodicSync sync = new PeriodicSync(account, "com.niyo.provider", extras, period);
		ContentResolver.addPeriodicSync(sync.account, sync.authority, extras, sync.period);
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
				task.execute(new URL("http://niyoapi.appspot.com/deleteCategory?name="+bean.getString("category")));
			} catch (Exception e) {
				ClientLog.e(LOG_TAG, "Errro!", e);
			}
		}
		
		return false;
	}

	private void getCategories() {
		
		ServiceCaller caller = new ServiceCaller() {
			
			@Override
			public void success(Object data) {
				
				if (data != null){
					JSONObject categoriesData = (JSONObject)data;
					JSONArray categories = null;
					try {
						categories = categoriesData.getJSONArray("tasks");
					} catch (JSONException e) {
						ClientLog.e(LOG_TAG, "Error!", e);
					}
					setCategories(categories);
				}
				else{
					ClientLog.d(LOG_TAG, "categories list is empty in activity");
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
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		getContentResolver().unregisterContentObserver(mObserver);
	}

	private void setCategories(JSONArray categories) {
		ClientLog.d(LOG_TAG, "setCategories started with "+categories);
		if (getAdapter() != null)
		{
			getAdapter().setList(categories);
			getAdapter().notifyDataSetChanged();
		}
		else
		{
			setAdapter(new CategoriesListAdapter(this));
			getAdapter().setList(categories);
			getList().setAdapter(getAdapter());
		}
	}
	
	/**
	 * Just to add something for the git process
	 */
	@Override
    protected Dialog onCreateDialog(int id) {
		switch (id) {
			case ADD_NEW_CATEGORY_DIALOG:
	            // This example shows how to add a custom layout to an AlertDialog
	            LayoutInflater factory = LayoutInflater.from(this);
	            final View textEntryView = factory.inflate(R.layout.add_category_dialog, null);
	            return new AlertDialog.Builder(CategoriesActivity.this)
	                .setIcon(R.drawable.alert_dialog_icon)
	                .setTitle("Add a category")
	                .setView(textEntryView)
	                .setPositiveButton("Add category", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                    	TextView nameView = (TextView)textEntryView.findViewById(R.id.categoryNameEdit);
	                        addCategory(nameView.getText());
	                    }
	                })
	                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	
	                        /* User clicked cancel so do some stuff */
	                    }
	                })
	                .create();
			}
		return null;
	}
	
	protected void addCategory(CharSequence name) {
		ClientLog.d(LOG_TAG, "name is "+name);
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("name", name.toString()));
		try {
			PostJsonTask task = new PostJsonTask(new UrlEncodedFormEntity(params, HTTP.UTF_8), this);
			try {
				URL url = new URL("http://niyoapi.appspot.com/addCategory");
				task.execute(url);
			} catch (MalformedURLException e) {
				ClientLog.e(LOG_TAG, "Error!", e);
			}
			
		} catch (Exception e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		
		addCategoryToProvider(name);
	}

	private void addCategoryToProvider(CharSequence name) {
		JSONArray oldTasks = getAdapter().getList();
		try {
			JSONObject newCategory = new JSONObject("{category:"+name+",\"tasks\":[]}");
			JSONArray newTasks = new JSONArray();
			for (int i = 0; i< oldTasks.length(); i++){
				if (oldTasks.get(i) instanceof JSONObject){
					newTasks.put(oldTasks.getJSONObject(i));
				}
			}
			
			newTasks.put(newCategory);
			
			JsonDbInsertTask task = new JsonDbInsertTask(this);
			String[] params = new String[]{NiyoContentProvider.AUTHORITY+"/tasks", "{\"tasks\":"+newTasks.toString()+"}"};
			
			task.execute(params);
			
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		
	}

	private ListView getList() {
		
		return (ListView)findViewById(R.id.categoriesList);
	}

	private void setUri(Uri uri)
	{
		mUri = uri;
	}
	
	private Uri getUri()
	{
		return mUri;
	}

	public CategoriesListAdapter getAdapter() {
		return mAdapter;
	}

	public void setAdapter(CategoriesListAdapter adapter) {
		mAdapter = adapter;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
		
		if (adapterView.getItemIdAtPosition(position) == CategoriesListAdapter.ADD_CATEGORY_ITEM_ID){
			showDialog(ADD_NEW_CATEGORY_DIALOG);
		}
		else
		{
			JSONObject json = (JSONObject) adapterView.getItemAtPosition(position);
			
			try {
				Intent intent = TasksActivity.getCreationIntent(this, json.getString("category"));
				startActivity(intent);
			} catch (JSONException e) {
				ClientLog.e(LOG_TAG, "Error!", e);
			}
		}
		
	}
}