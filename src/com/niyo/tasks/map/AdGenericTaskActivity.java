package com.niyo.tasks.map;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.niyo.ClientLog;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.Utils;
import com.niyo.categories.AddCategoryActivity;
import com.niyo.categories.CategoriesListAdapter;
import com.niyo.categories.CategoryBean;
import com.niyo.data.DBJsonFetchTask;
import com.niyo.data.NiyoContentProvider;
import com.niyo.tasks.AddGenericTaskTask;
import com.niyo.tasks.LocationTask;

public class AdGenericTaskActivity extends MapActivity implements OnClickListener, OnItemClickListener {
	
	private static final String LOG_TAG = AdGenericTaskActivity.class.getSimpleName();
	private static final String TASK_ID_EXTRA = "taskID";
	private static final int CATEGORY_ADD_REQUEST = 0;
	public static final String CATEGORY_DATA = "categoryData";
	public static final int RESULT_ADD_CATEGORY = 333;
	private GeoPoint mSeletctedAddress;
	private CategoriesListAdapter mCategoriesAdapter;
	private List<CategoryBean> mCategories;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_generic_task_layout);
		findViewById(R.id.searchBtn).setOnClickListener(this);
		EditText searchEdit = (EditText)findViewById(R.id.searchEdit);
		findViewById(R.id.showCategories).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showCategories();
			}
		});
		
		findViewById(R.id.mapBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showMap();
				
			}
		});
		getCategoryListView().setOnItemClickListener(this);
		searchEdit.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				
				onClick(v);
				return true;
			}
		});
		
		findViewById(R.id.createTask).setOnClickListener(getCreateTaskClickListener());
		showCategories();
		List<CategoryBean> initialCategoryList = new ArrayList<CategoryBean>();
		
		Integer specialId = new Integer(CategoriesListAdapter.ADD_CATEGORY_ITEM_ID);
		initialCategoryList.add(new CategoryBean("Add a category", specialId.toString()));
		mCategories = initialCategoryList;
		populateCategoriesList();
		
		if (isEditMode()){
			TextView addTask = (TextView)findViewById(R.id.createTask);
			addTask.setText("Save Task");
			asyncCallForTasks();
		}
	}
	
	private ListView getCategoryListView() {
		
		return (ListView)findViewById(R.id.categoryList);
	}

	private void showCategories() {
		findViewById(R.id.map_view).setVisibility(View.GONE);
		findViewById(R.id.categoryList).setVisibility(View.VISIBLE);
		Utils.hideKeyboard(this, findViewById(R.id.taskTitleLabel));
	}
	
	private void showMap(){
		findViewById(R.id.categoryList).setVisibility(View.GONE);
		findViewById(R.id.map_view).setVisibility(View.VISIBLE);
		Utils.hideKeyboard(this, findViewById(R.id.taskTitleLabel));
	}

	private void populateCategoriesList() {
		
		List<CategoryBean> categories = getCategories();
		if (getCategoriesAdapter() != null)
		{
			getCategoriesAdapter().setList(categories);
			getCategoriesAdapter().notifyDataSetChanged();
		}
		else
		{
			setCategoriesAdapter(new CategoriesListAdapter(this));
			getCategoriesAdapter().setList(categories);
			
			ListView categoriesListView = (ListView)findViewById(R.id.categoryList);
			categoriesListView.setAdapter(getCategoriesAdapter());
		}
	}

	protected void asyncCallForTasks() {

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
						for(int i = 0; i < tasks.length(); i++){
							JSONObject task;

							task = tasks.getJSONObject(i);

							if (task.getString("id").equals(getTaskIdFromIntent())){
								populateTask(task);
							}
						}
					} catch (JSONException e) {
						ClientLog.e(LOG_TAG, "Error!", e);
					}
				}
				else{
					ClientLog.e(LOG_TAG, "tasks list is empty in activity");
				}
				
				
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

	protected String getTaskIdFromIntent() {
		return getIntent().getStringExtra(TASK_ID_EXTRA);
	}

	private void populateTask(JSONObject task) throws JSONException {
		
		TextView taskTitle = (TextView)findViewById(R.id.taskTitleLabel);
		taskTitle.setTag(task.getString("id"));
		taskTitle.setText(task.getString("content"));
		
		ClientLog.d(LOG_TAG, "task is "+task);
		
		String userInputAddress = task.getString("userInputAddress");
		
		EditText addressInput = (EditText)findViewById(R.id.searchEdit);
		addressInput.setText(userInputAddress);
		
		if (!(userInputAddress.equals("null")) && !TextUtils.isEmpty(userInputAddress)){
			showMarker(userInputAddress);
			showMap();
		}
		
		List<CategoryBean> categories = createCategoriesList(task);
		
		for (CategoryBean categoryBean : categories) {
			addToCategoryList(categoryBean);
		}
		populateCategoriesList();
		
	}

	private List<CategoryBean> createCategoriesList(JSONObject task) throws JSONException {
		
		JSONArray categoriesJson = task.getJSONArray("categories");
		
		List<CategoryBean> result = new ArrayList<CategoryBean>();
		
		for (int i = 0; i < categoriesJson.length(); i++){
			
			String name = categoriesJson.getJSONObject(i).getString("name");
			String id = categoriesJson.getJSONObject(i).getString("id");
			result.add(new CategoryBean(name, id));
		}
		
		return result;
	}

	private boolean isEditMode() {
		return getIntent().getStringExtra(TASK_ID_EXTRA) != null;
	}

	private OnClickListener getCreateTaskClickListener() {
		
		final ServiceCaller caller = new ServiceCaller() {
			
			@Override
			public void success(Object data) {
				ClientLog.d(LOG_TAG, "task added");
				finish();
			}
			
			@Override
			public void failure(Object data, String description) {
				
			}
		};
		
		OnClickListener result = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				TextView taskTitle = (TextView)findViewById(R.id.taskTitleLabel);
				
				if (TextUtils.isEmpty(taskTitle.getText())){
					Toast.makeText(AdGenericTaskActivity.this, "You must enter a task title", Toast.LENGTH_LONG);
					return;
				}
				
				
				
				EditText addressInput = (EditText)findViewById(R.id.searchEdit);
				
				String id = null;
				if (taskTitle.getTag() != null){
					id = (String)taskTitle.getTag();
				}
				else{
					id = generateId().toString();
				}
				LocationTask locTask = new LocationTask(id, mSeletctedAddress, taskTitle.getText().toString(),
						addressInput.getText().toString(), removeAddItem(getCategories()));
				LocationTask[] params = new LocationTask[]{locTask};
				
				AddGenericTaskTask task = new AddGenericTaskTask(AdGenericTaskActivity.this, caller);
				task.execute(params);
			}
		};
		return result;
	}
	
	protected List<CategoryBean> removeAddItem(List<CategoryBean> categories) {
		categories.remove(categories.size()-1);
		return categories;
	}

	private UUID generateId() {
		return UUID.randomUUID();
	}

	public static Intent getCreationIntent(Activity activity, String taskId){
		
		Intent intent = new Intent(activity, AdGenericTaskActivity.class);
		intent.putExtra(TASK_ID_EXTRA, taskId);
		return intent;
	}
	
	@Override
	public void onClick(View v) {
		
		EditText addressEdit = (EditText)findViewById(R.id.searchEdit);
//		Utils.hideKeyboard(this, addressEdit);
		String userAddress = addressEdit.getText().toString();
		
		showMarker(userAddress);
	}

	private void showMarker(String userAddress) {
		Geocoder coder = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addresses = coder.getFromLocationName(userAddress, 5);
			
			if (addresses.size() <= 0){
				Toast.makeText(this, "No results for "+userAddress, Toast.LENGTH_LONG).show();
			}
			else{
				if (addresses.size() == 1){
					mSeletctedAddress = createGeoPoint(addresses.get(0));
				}
				showResults(addresses);
			}
			
		} catch (IOException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
	}

	private GeoPoint createGeoPoint(Address address) {
		
		Double lat1e6 = new Double(address.getLatitude()*1e6);
		Double lon1e6 = new Double(address.getLongitude()*1e6);
		
		GeoPoint result = new GeoPoint(lat1e6.intValue(), lon1e6.intValue());
		return result;
	}

	private void showResults(List<Address> addresses) {
		
		showMap();
		MapView mapView = (MapView)findViewById(R.id.map_view);
		List<Overlay> overlays = mapView.getOverlays();
		Resources r = getResources();
		AutoItemizedOverlay markers = new AutoItemizedOverlay(r.getDrawable(R.drawable.marker));
		for (Address address : addresses) {
			
			Double lat1E6 = new Double(address.getLatitude()*1E6);
			Double lon1E6 = new Double(address.getLongitude()*1E6);
			GeoPoint point = new GeoPoint(lat1E6.intValue(), lon1E6.intValue());
			
			markers.addNewItem(point, "markerText", "snippet");
		}
		overlays.add(markers);
		
		centerAndZoom(addresses);
		
	}

	private void centerAndZoom(List<Address> addresses) {
		
		MapView mapView = (MapView)findViewById(R.id.map_view);
		MapController mapController = mapView.getController();
		
		Double lat1E6 = new Double(addresses.get(0).getLatitude()*1E6);
		Double lon1E6 = new Double(addresses.get(0).getLongitude()*1E6);
		GeoPoint point = new GeoPoint(lat1E6.intValue(), lon1E6.intValue());
		
		mapController.setCenter(point);
		mapController.setZoom(18);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public CategoriesListAdapter getCategoriesAdapter() {
		return mCategoriesAdapter;
	}

	public void setCategoriesAdapter(CategoriesListAdapter categoriesAdapter) {
		mCategoriesAdapter = categoriesAdapter;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
		
		
		if (adapterView.getItemIdAtPosition(position) == CategoriesListAdapter.ADD_CATEGORY_ITEM_ID){
			Intent intent = AddCategoryActivity.getCreationIntent(this);
			startActivityForResult(intent, CATEGORY_ADD_REQUEST);
		}
	}
	
	@Override
	protected final void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		CategoryBean category = (CategoryBean)data.getSerializableExtra(CATEGORY_DATA);
		addToCategoryList(category);
		populateCategoriesList();
	}

	public List<CategoryBean> getCategories() {
		return mCategories;
	}

	public void addToCategoryList(CategoryBean category) {
		mCategories.add(0, category);
	}

}
