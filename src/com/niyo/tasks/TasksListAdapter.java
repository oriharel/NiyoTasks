package com.niyo.tasks;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.niyo.ClientLog;
import com.niyo.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TasksListAdapter extends BaseAdapter {

	private static final String LOG_TAG = TasksListAdapter.class.getSimpleName();
	private Activity mActivity;
	private List<JSONObject> mTasks;
//	protected static final int ADD_TASK_TYPE = 0;
//	public static final int ADD_TASK_ITEM_ID = 6367;
//	protected static final int REGULAR_TASK_TYPE = 1;
	
	public TasksListAdapter(Activity activity){
		setActivity(activity);
	}
	
	public void setList(List<JSONObject> tasks){
		
		mTasks = tasks;
	}
	
//	@Override
//	public int getViewTypeCount()
//	{
//		return 2;
//	}
//	
//	@Override
//	public int getItemViewType (int position)
//	{
//		if (getCount() == 0 || position == getCount()-1)
//		{
//			return ADD_TASK_TYPE;
//		}
//		else
//		{
//			return REGULAR_TASK_TYPE;
//		}
//	}
	
	@Override
	public int getCount() {
		return mTasks.size();
	}

	@Override
	public Object getItem(int position) {
		try {
			return mTasks.get(position);
		} catch (Exception e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		
		return null;
	}

	@Override
	public long getItemId(int position) {
//		if (getCount() == 0 || position == getCount()-1){
//			return ADD_TASK_ITEM_ID;
//		}
//		else{
			return position;
//		}
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		TaskHolder holder;
//		int type = getItemViewType(position);
//		switch (type) 
//		{
//			case ADD_TASK_TYPE:
//			{
//				convertView = getActivity().getLayoutInflater().inflate(R.layout.add_new_task_item, null);
//				return convertView;
//			}
//			default:
//			{
				if (convertView == null)
				{
					convertView = getActivity().getLayoutInflater().inflate(R.layout.task_list_item, null);
					holder = new TaskHolder();
					holder.taskName = (TextView)convertView.findViewById(R.id.taskName);
					convertView.setTag(holder);
				}
				else
				{
					holder = (TaskHolder)convertView.getTag();
				}
				
				try {
					if (getItem(position) instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)getItem(position);
						holder.taskName.setText(jsonObj.getString("content"));
					}
					
				} catch (JSONException e) {
					ClientLog.e(LOG_TAG, "Error!", e);
				}
//			}
//		}
		
		
		return convertView;
		
	}
	public Activity getActivity() {
		return mActivity;
	}
	public void setActivity(Activity activity) {
		mActivity = activity;
	}
	
	static class TaskHolder{
		TextView taskName;
	}

}
