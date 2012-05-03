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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.data.AddCategoryToProviderTask;
import com.niyo.data.PostJsonTask;
import com.niyo.tasks.map.AdGenericTaskActivity;

public class AddCategoryActivity extends NiyoAbstractActivity implements OnItemClickListener {

	private static final String LOG_TAG = AddCategoryActivity.class.getSimpleName();
	private FoursqureCategoryAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ClientLog.d(LOG_TAG, "onCreate called");
		setContentView(R.layout.add_category_layout);
		getList().setOnItemClickListener(this);
		getList().setTextFilterEnabled(true);
		populateList(getMyApplication().getFoursqaureVenues());
	}
	
	private void populateList(JSONArray categories) {
		
		if (getAdapter() != null){
			
			getAdapter().setFoursquareCategories(categories);
			getAdapter().notifyDataSetChanged();
		}
		else{
			
			setAdapter(new FoursqureCategoryAdapter(this));
			getAdapter().setFoursquareCategories(categories);
			getList().setAdapter(getAdapter());
		}
		
		
	}

	private ListView getList() {

		return (ListView)findViewById(R.id.foursqrCategoriesList);
	}

	public static Intent getCreationIntent(Activity activity){
		
		Intent result = new Intent(activity, AddCategoryActivity.class);
		return result;
	}
	
	protected void addCategory(CharSequence name, String id) {
		
		CategoryBean result = new CategoryBean(name.toString(), id);
		
		Intent intent = new Intent();
		intent.putExtra(AdGenericTaskActivity.CATEGORY_DATA, result);
		
		setResult(AdGenericTaskActivity.RESULT_ADD_CATEGORY, intent);
		finish();
	}
	

	public FoursqureCategoryAdapter getAdapter() {
		return mAdapter;
	}

	public void setAdapter(FoursqureCategoryAdapter adapter) {
		mAdapter = adapter;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View itemLayout, int position, long arg3) {
		
		JSONObject category = (JSONObject)adapterView.getItemAtPosition(position);
		ClientLog.d(LOG_TAG, "item clicked with "+category);
		try {
			if (category.has("categories")){
				JSONArray subCategories = category.getJSONArray("categories");
				if (subCategories.length() > 0){
					populateList(subCategories);
				}
				else{
					addCategory(category.getString("name"), category.getString("id"));
				}
			}
			
			else{
				addCategory(category.getString("name"), category.getString("id"));
			}
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error", e);
		}
		
		
	}
}
