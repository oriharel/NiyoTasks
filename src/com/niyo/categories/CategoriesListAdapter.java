package com.niyo.categories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.niyo.ClientLog;
import com.niyo.R;

public class CategoriesListAdapter extends BaseAdapter {

	private static final String LOG_TAG = CategoriesListAdapter.class.getSimpleName();
	private Activity mActivity;
	private JSONArray mCategories;
	protected static final int ADD_CATEGORY_TYPE = 0;
	public static final int ADD_CATEGORY_ITEM_ID = 6367;
	public static final int ADD_GENERIC_ITEM_ID = 6366;
	protected static final int REGULAR_CATEGORY_TYPE = 1;
	protected static final int AD_GENERIC_TYPE_TYPE = 2;
	
	public CategoriesListAdapter(Activity activity){
		setActivity(activity);
	}
	
	public void setList(JSONArray categories){
		mCategories = categories;
		mCategories.put(0);
		mCategories.put(1);
	}
	
	public JSONArray getList(){
		return mCategories;
	}
	
	@Override
	public int getViewTypeCount()
	{
		return 3;
	}
	
	@Override
	public int getItemViewType (int position)
	{
		
		if (getCount() == 0 || position == getCount()-2)
		{
			return ADD_CATEGORY_TYPE;
		}
		else if (position == getCount()-1)
		{
			return AD_GENERIC_TYPE_TYPE;
		}
		else
		{
			return REGULAR_CATEGORY_TYPE;
		}
	}
	
	@Override
	public int getCount() {
		return mCategories.length();
	}

	@Override
	public Object getItem(int position) {
		try {
			return mCategories.get(position);
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		if (getCount() == 0 || position == getCount()-2){
			return ADD_CATEGORY_ITEM_ID;
		}
		else if (position == getCount()-1){
			return ADD_GENERIC_ITEM_ID;
		}
		else{
			return position;
		}
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		CategoryHolder holder;
		int type = getItemViewType(position);
		switch (type) 
		{
			case ADD_CATEGORY_TYPE:
			{
				convertView = getActivity().getLayoutInflater().inflate(R.layout.add_new_category_item, null);
				return convertView;
			}
			case AD_GENERIC_TYPE_TYPE:
			{
				convertView = getActivity().getLayoutInflater().inflate(R.layout.add_new_category_item, null);
				TextView text = (TextView)convertView.findViewById(R.id.addCategoryItem);
				text.setText("Add a task");
				return convertView;
			}
			default:
			{
				if (convertView == null)
				{
					convertView = getActivity().getLayoutInflater().inflate(R.layout.category_list_item, null);
					holder = new CategoryHolder();
					holder.categoryName = (TextView)convertView.findViewById(R.id.categoryName);
					holder.numOfTasks = (TextView)convertView.findViewById(R.id.numOfTasks);
					convertView.setTag(holder);
				}
				else
				{
					holder = (CategoryHolder)convertView.getTag();
				}
				
				try {
					JSONObject item = (JSONObject)getItem(position);
					holder.categoryName.setText(item.getString("category"));
					JSONArray tasks = item.getJSONArray("tasks");
					if (tasks.length() > 0){
						holder.numOfTasks.setVisibility(View.VISIBLE);
						holder.numOfTasks.setText(Integer.toString(tasks.length()));
					}
					else{
						holder.numOfTasks.setVisibility(View.GONE);
					}
				} catch (JSONException e) {
					ClientLog.e(LOG_TAG, "Error!", e);
				}
			}
		}
		
		
		return convertView;
		
	}
	public Activity getActivity() {
		return mActivity;
	}
	public void setActivity(Activity activity) {
		mActivity = activity;
	}
	
	static class CategoryHolder{
		TextView categoryName;
		TextView numOfTasks;
	}

}
