package com.niyo.categories;

import java.util.List;

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
	private List<CategoryBean> mCategories;
	protected static final int ADD_CATEGORY_TYPE = 0;
	public static final int ADD_CATEGORY_ITEM_ID = 6367;
	public static final int ADD_GENERIC_ITEM_ID = 6366;
	protected static final int REGULAR_CATEGORY_TYPE = 1;
	
	public CategoriesListAdapter(Activity activity){
		setActivity(activity);
	}
	
	public void setList(List<CategoryBean> categories){
		mCategories = categories;
	}
	
	public List<CategoryBean> getList(){
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
		
		if (getItemId(position) == ADD_CATEGORY_ITEM_ID)
		{
			return ADD_CATEGORY_TYPE;
		}
		else
		{
			return REGULAR_CATEGORY_TYPE;
		}
	}
	
	@Override
	public int getCount() {
		return mCategories.size();
	}

	@Override
	public CategoryBean getItem(int position) {
		return mCategories.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		try{
			
			Long itemId = Long.parseLong(getItem(position).getId());
			if (itemId == ADD_CATEGORY_ITEM_ID){
				return ADD_CATEGORY_ITEM_ID;
			}
			else{
				return position;
			}
		}
		catch (NumberFormatException e){
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
			default:
			{
				if (convertView == null)
				{
					convertView = getActivity().getLayoutInflater().inflate(R.layout.category_list_item, null);
					holder = new CategoryHolder();
					holder.categoryName = (TextView)convertView.findViewById(R.id.categoryName);
					convertView.setTag(holder);
				}
				else
				{
					holder = (CategoryHolder)convertView.getTag();
				}
				
				CategoryBean category = getItem(position);
				holder.categoryName.setText(category.getName());
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
	}

}
