package com.niyo.tasks;

import java.net.MalformedURLException;
import java.net.URL;

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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.StringUtils;
import com.niyo.data.DBJsonFetchTask;
import com.niyo.data.NiyoContentProvider;
import com.niyo.data.PostJsonTask;

public class AddTaskActivity extends NiyoAbstractActivity implements OnItemClickListener {
	
	private static final String LOG_TAG = AddTaskActivity.class.getSimpleName();
	private static final String CATEGORY_EXTRA = "category";
	private ContentObserver mObserver;
	private SuggestionsTasksListAdapter mAdapter;
	
	private Uri mUri;
	
	private TextWatcher filterTextWatcher = new TextWatcher() {

	    public void afterTextChanged(Editable s) {
	    }

	    public void beforeTextChanged(CharSequence s, int start, int count,
	            int after) {
	    }

	    public void onTextChanged(CharSequence s, int start, int before,
	            int count) {

	         if (getAdapter()==null) {
	             return;
	         }

	         getAdapter().getFilter().filter(s);

	    }
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ClientLog.d(LOG_TAG, "onCreate called");
		setContentView(R.layout.add_task_layout);
		getList().setOnItemClickListener(this);
		
		
		getList().setTextFilterEnabled(true);
		EditText addItemEdit = (EditText)findViewById(R.id.taskNameEdit);
		addItemEdit.addTextChangedListener(filterTextWatcher);
		mObserver = new ContentObserver(getHandler()) {
			public void onChange(boolean selfChange) 
			{
				ClientLog.d(LOG_TAG, "onChange started");
				getSuggestions();
			}
		};

		URL url= null;
		
		try {
			url = new URL("http://niyoapi.appspot.com/getFlatTasks");
		} catch (MalformedURLException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
			return;
		}
		
		Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+url.getPath());
		setUri(uri);
		
		
		getContentResolver().registerContentObserver(uri, false, mObserver);
		
		getSuggestions();
	}
	
	protected void getSuggestions() {
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
					setSuggestions(tasks);
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

	protected void setSuggestions(JSONArray tasks) {
		if (getAdapter() != null)
		{
			getAdapter().notifyDataSetChanged();
		}
		else
		{
			setAdapter(new SuggestionsTasksListAdapter(this, R.layout.task_suggestion_item_layout,
					R.id.taskSuggestionName, StringUtils.toTasksList(tasks)));
			getList().setAdapter(getAdapter());
		}
	}
	
	
	
	private ListView getList() {

		return (ListView)findViewById(R.id.tasksSuggestionsList);
	}

	public static Intent getCreationIntent(Activity activity, String category){
		
		Intent result = new Intent(activity, AddTaskActivity.class);
		result.putExtra(CATEGORY_EXTRA, category);
		return result;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		getContentResolver().unregisterContentObserver(mObserver);
	}
	
	public void addClicked(View v){
		
		TextView taskName = (TextView)findViewById(R.id.taskNameEdit);
		addTaskFromView(taskName);
	}
	
	private void addTaskFromView(TextView taskName){

		if (!TextUtils.isEmpty(taskName.getText())){
			
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

	public Uri getUri() {
		return mUri;
	}

	public void setUri(Uri uri) {
		mUri = uri;
	}

	public SuggestionsTasksListAdapter getAdapter() {
		return mAdapter;
	}

	public void setAdapter(SuggestionsTasksListAdapter adapter) {
		mAdapter = adapter;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View itemLayout, int arg2, long arg3) {
		ClientLog.d(LOG_TAG, "item clicked");
		TextView nameView = (TextView)itemLayout.findViewById(R.id.taskSuggestionName);
		addTaskFromView(nameView);
		
	}
	

}
