package com.niyo.categories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.niyo.ClientLog;
import com.niyo.NiyoAbstractActivity;
import com.niyo.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FoursqureCategoryAdapter extends BaseAdapter {
	
	private NiyoAbstractActivity mActivity;
	private JSONArray mFoursquareCategories;
	private static final String LOG_TAG = FoursqureCategoryAdapter.class.getSimpleName();
	
	
	public FoursqureCategoryAdapter(NiyoAbstractActivity activity){
		
		setActivity(activity);
		
//		mFoursquareCategories = activity.getMyApplication().getFoursqaureVenues();
	}

	@Override
	public int getCount() {
		return getFoursquareCategories().length();
	}

	@Override
	public JSONObject getItem(int position) {
		try {
			return getFoursquareCategories().getJSONObject(position);
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Holder holder;
		if (convertView == null)
		{
			convertView = getActivity().getLayoutInflater().inflate(R.layout.foursqaure_cat_item, null);
			holder = new Holder();
			holder.categoryName = (TextView)convertView.findViewById(R.id.categoryName);
			holder.moreCategories = (ImageView)convertView.findViewById(R.id.moreCategories);
			convertView.setTag(holder);
		}
		else
		{
			holder = (Holder)convertView.getTag();
		}
		
		try {
			
			JSONObject category = getItem(position);
			holder.categoryName.setText(category.getString("name"));
			
			if (category.has("categories") && category.getJSONArray("categories").length() > 0){
				holder.moreCategories.setVisibility(View.VISIBLE);
			}
			else{
				holder.moreCategories.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			ClientLog.e(LOG_TAG, "Error! for position "+position, e);
		}
		
		return convertView;
	}

	public JSONArray getFoursquareCategories() {
		return mFoursquareCategories;
	}

	public void setFoursquareCategories(JSONArray foursquareCategories) {
		mFoursquareCategories = foursquareCategories;
	}
	
	public NiyoAbstractActivity getActivity() {
		return mActivity;
	}

	public void setActivity(NiyoAbstractActivity activity) {
		mActivity = activity;
	}

	static class Holder{
		
		TextView categoryName;
		ImageView categoryIcon;
		ImageView moreCategories;
	}

}
