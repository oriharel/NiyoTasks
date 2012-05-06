package com.niyo.auto;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.SettingsManager;
import com.niyo.auto.map.AutoMapActivity;
import com.niyo.data.DBJsonFetchTask;
import com.niyo.data.NiyoContentProvider;
import com.niyo.tasks.CategoryTasksActivity;

public class AutoActivity extends NiyoAbstractActivity {
    /** Called when the activity is first created. */
	
	private static final String LOG_TAG = AutoActivity.class.getSimpleName();
	
	private static final String BOX_TITLE_1_KEY = "boxTitle1";
	private static final String BOX_TITLE_2_KEY = "boxTitle2";
	private static final String BOX_TITLE_3_KEY = "boxTitle3";
	private static final String BOX_TITLE_4_KEY = "boxTitle4";
	private static final String BOX_TITLE_5_KEY = "boxTitle5";
	private static final String BOX_TITLE_6_KEY = "boxTitle6";
	
	private static final String BOX_LAT_1_KEY = "boxLat1";
	private static final String BOX_LAT_2_KEY = "boxLat2";
	private static final String BOX_LAT_3_KEY = "boxLat3";
	private static final String BOX_LAT_4_KEY = "boxLat4";
	private static final String BOX_LAT_5_KEY = "boxLat5";
	private static final String BOX_LAT_6_KEY = "boxLat6";
	
	private static final String BOX_LON_1_KEY = "boxLon1";
	private static final String BOX_LON_2_KEY = "boxLon2";
	private static final String BOX_LON_3_KEY = "boxLon3";
	private static final String BOX_LON_4_KEY = "boxLon4";
	private static final String BOX_LON_5_KEY = "boxLon5";
	private static final String BOX_LON_6_KEY = "boxLon6";

	private static final int GO_TO_TODO_LIST_CONTEXT_MENU_ITEM = 0;

	private static final int RESTORE_DEFAULTS_CONTEXT_MENU_ITEM = 1;

	public static final String SIX_PACK_CHANGED = "sixPackChangedKey";
	
	private Uri mUri;
	private JSONArray mTasks;
	private Boolean mTasksReady = false;
	
	private Map<Integer, String> mDefaultsTitles;
	private Map<Integer, String> mDefaultsLocations;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.where_to_layout);
        Log.d(LOG_TAG, "onCreate started");
        
        setupBoxBtnsListeners();
        saveDefaults();
        
        URL url= null;
        
		try {
			url = new URL("http://niyoapi.appspot.com/tasks");
		} catch (MalformedURLException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
			return;
		}
		
		
		Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+url.getPath());
		setUri(uri);
        loadTasksFromDb();
        
    }
    

    private void saveDefaults() {
		
    	mDefaultsTitles = new HashMap<Integer, String>();
    	mDefaultsLocations = new HashMap<Integer, String>();
    	
    	List<Integer> boxIds = new ArrayList<Integer>();
    	
    	boxIds.add(R.id.boxBtn1);
    	boxIds.add(R.id.boxBtn2);
    	boxIds.add(R.id.boxBtn3);
    	boxIds.add(R.id.boxBtn4);
    	boxIds.add(R.id.boxBtn5);
    	boxIds.add(R.id.boxBtn6);
    	
    	for (Integer boxId : boxIds) {
			
    		Button box = (Button)findViewById(boxId);
    		mDefaultsTitles.put(box.getId(), box.getText().toString());
    		mDefaultsLocations.put(box.getId(), (String)box.getTag());
		}
    	
    	
	}


	@Override
    protected void onResume(){
    	super.onResume();
    	setupBoxes();
    }
	
	
	private void setupBoxes() {
    	
		if (hasUSerChanged()){
			List<Integer> boxIds = new ArrayList<Integer>();
	    	
	    	boxIds.add(R.id.boxBtn1);
	    	boxIds.add(R.id.boxBtn2);
	    	boxIds.add(R.id.boxBtn3);
	    	boxIds.add(R.id.boxBtn4);
	    	boxIds.add(R.id.boxBtn5);
	    	boxIds.add(R.id.boxBtn6);
	    	
	    	for (Integer boxId : boxIds) {
				
	    		Button btn = (Button)findViewById(boxId);
	    		
	    		String title = SettingsManager.getString(this, getTitleKey(btn));
	    		btn.setText(title);
	    		Float lat = SettingsManager.getFloat(this, getLatKey(btn), 0);
	    		Float lon = SettingsManager.getFloat(this, getLonKey(btn), 0);
	    		
	    		if (! (lat == 0 || lon == 0)){
	    			String tag = lat+","+lon;
	    			btn.setTag(tag);
	    		}
			}
		}
    	
		
	}
	
	private boolean hasUSerChanged() {
		return SettingsManager.getBoolean(this, SIX_PACK_CHANGED, false);
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) 
	{
		ClientLog.d(LOG_TAG, "onCreateOptionsMenu started");
		try {
			MenuItem settingsMenuItem1 = menu.add(0, GO_TO_TODO_LIST_CONTEXT_MENU_ITEM, 0, "Go To List");
			MenuItem settingsMenuItem2 = menu.add(0, RESTORE_DEFAULTS_CONTEXT_MENU_ITEM, 0, "Restore Defaults");
			settingsMenuItem1.setIcon(R.drawable.ic_menu_agenda);
			settingsMenuItem2.setIcon(R.drawable.ic_menu_preferences);
		} catch (Exception e) 
		{
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem menuItem) 
	{
    	if (menuItem.getItemId() == GO_TO_TODO_LIST_CONTEXT_MENU_ITEM){
    		
    		Intent intent = CategoryTasksActivity.getCreationIntent(this);
        	startActivity(intent);
        	return true;
    	}
    	else if (menuItem.getItemId() == RESTORE_DEFAULTS_CONTEXT_MENU_ITEM){
    		
    		resotreDefaults();
        	return true;
    	}
    	else{
    		return false;
    	}
    	
	}

	private void resotreDefaults() {
		
		List<Integer> boxIds = new ArrayList<Integer>();
    	
    	boxIds.add(R.id.boxBtn1);
    	boxIds.add(R.id.boxBtn2);
    	boxIds.add(R.id.boxBtn3);
    	boxIds.add(R.id.boxBtn4);
    	boxIds.add(R.id.boxBtn5);
    	boxIds.add(R.id.boxBtn6);
    	for (Integer boxId : boxIds) {
			Button btn = (Button)findViewById(boxId);
			
			btn.setText(mDefaultsTitles.get(boxId));
			btn.setTag(mDefaultsLocations.get(boxId));
			
			SettingsManager.removeKey(this, getTitleKey(btn));
			SettingsManager.removeKey(this, getLatKey(btn));
			SettingsManager.removeKey(this, getLonKey(btn));
		}
    	
    	SettingsManager.setBoolean(this, SIX_PACK_CHANGED, false);
	}


	private void setupBoxBtnsListeners() {
		
    	findViewById(R.id.boxBtn1).setOnLongClickListener(getBoxLongClickListener());
    	findViewById(R.id.boxBtn2).setOnLongClickListener(getBoxLongClickListener());
    	findViewById(R.id.boxBtn3).setOnLongClickListener(getBoxLongClickListener());
    	findViewById(R.id.boxBtn4).setOnLongClickListener(getBoxLongClickListener());
    	findViewById(R.id.boxBtn5).setOnLongClickListener(getBoxLongClickListener());
    	findViewById(R.id.boxBtn6).setOnLongClickListener(getBoxLongClickListener());
	}

	private OnLongClickListener getBoxLongClickListener() {
		
		OnLongClickListener result = new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				ClientLog.d(LOG_TAG, "locations of button is "+v.getTag());
				Intent intent = CreateAutoBoxAcitivty.getCreationIntent(AutoActivity.this, getTitleKey(v), getLatKey(v), getLonKey(v));
				startActivity(intent);
				return true;
			}
		};
		return result;
	}

	protected String getLonKey(View v) {

		int boxId = v.getId();

		switch (boxId) {
		case R.id.boxBtn1:
			return BOX_LON_1_KEY;
		case R.id.boxBtn2:
			return BOX_LON_2_KEY;
		case R.id.boxBtn3:
			return BOX_LON_3_KEY;
		case R.id.boxBtn4:
			return BOX_LON_4_KEY;
		case R.id.boxBtn5:
			return BOX_LON_5_KEY;
		case R.id.boxBtn6:
			return BOX_LON_6_KEY;

		default:
			break;
		}

		return null;
	}

	protected String getLatKey(View v) {
		
		int boxId = v.getId();

		switch (boxId) {
		case R.id.boxBtn1:
			return BOX_LAT_1_KEY;
		case R.id.boxBtn2:
			return BOX_LAT_2_KEY;
		case R.id.boxBtn3:
			return BOX_LAT_3_KEY;
		case R.id.boxBtn4:
			return BOX_LAT_4_KEY;
		case R.id.boxBtn5:
			return BOX_LAT_5_KEY;
		case R.id.boxBtn6:
			return BOX_LAT_6_KEY;

		default:
			break;
		}

		return null;
	}

	protected String getTitleKey(View v) 
	{
		int boxId = v.getId();
		
		switch (boxId) {
		case R.id.boxBtn1:
			return BOX_TITLE_1_KEY;
		case R.id.boxBtn2:
			return BOX_TITLE_2_KEY;
		case R.id.boxBtn3:
			return BOX_TITLE_3_KEY;
		case R.id.boxBtn4:
			return BOX_TITLE_4_KEY;
		case R.id.boxBtn5:
			return BOX_TITLE_5_KEY;
		case R.id.boxBtn6:
			return BOX_TITLE_6_KEY;

		default:
			break;
		}
		
		return null;
	}

	private void loadTasksFromDb() {
		
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
    		TextView btn = (TextView)view;
    		
        	String[] coordinsatesStrArray = locationStr.split(",");
        	AutoPoint to = new AutoPoint(Double.parseDouble(coordinsatesStrArray[0]), Double.parseDouble(coordinsatesStrArray[1]), btn.getText().toString());
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
			JSONObject task = tasks.getJSONObject(i);
			JSONArray categories = task.getJSONArray("categories");
			
			for (int j = 0; j < categories.length(); j++){
				result.add(categories.getJSONObject(j).getString("id"));
			}
			
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