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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import com.niyo.ServiceCaller;
import com.niyo.data.AddCategoryToProviderTask;
import com.niyo.data.PostJsonTask;

public class AddCategoryActivity extends NiyoAbstractActivity implements OnItemClickListener {

	private static final String LOG_TAG = AddCategoryActivity.class.getSimpleName();
	private FoursqureCategoryAdapter mAdapter;
	
//	private TextWatcher filterTextWatcher = new TextWatcher() {
//
//	    public void afterTextChanged(Editable s) {
//	    }
//
//	    public void beforeTextChanged(CharSequence s, int start, int count,
//	            int after) {
//	    }
//
//	    public void onTextChanged(CharSequence s, int start, int before,
//	            int count) {
//
//	    }
//	};
	
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
	
//	public void addClicked(View v){
//		
//		TextView categoryNameEdit = (TextView)findViewById(R.id.categoryNameEdit);
//		String id = "";
//		if (categoryNameEdit.getTag() != null){
//			
//			id = (String)categoryNameEdit.getTag();
//		}
//		addCategory(categoryNameEdit.getText(), id);
//	}
	
	protected void addCategory(CharSequence name, String id) {
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
		
		addCategoryToProvider(name, id);
	}
	
	private void addCategoryToProvider(CharSequence name, String id) {
		
		ServiceCaller caller = new ServiceCaller() {
			
			@Override
			public void success(Object data) {
				finish();
				
			}
			
			@Override
			public void failure(Object data, String description) {
				// TODO Auto-generated method stub
				
			}
		};
		AddCategoryToProviderTask task = new AddCategoryToProviderTask(this, caller);
		String[] params = new String[2];
		params[0] = name.toString();
		params[1] = id.toString();
		task.execute(params);
		
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
