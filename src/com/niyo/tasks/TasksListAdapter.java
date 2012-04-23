package com.niyo.tasks;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.niyo.ClientLog;
import com.niyo.R;

public class TasksListAdapter extends BaseAdapter {

	private static final String LOG_TAG = TasksListAdapter.class.getSimpleName();
	private Activity mActivity;
	private List<JSONObject> mOpenTasks;
	private List<JSONObject> mCrossedTasks;
	public static final int ADD_TASK_TYPE = 0;
	public static final int OPEN_TASK_TYPE = 1;
	public static final int CROSS_TASK_TYPE = 2;
	public static final int CROSS_TASKS_LABEL_TYPE = 3;
	
	
	public TasksListAdapter(Activity activity){
		setActivity(activity);
	}
	
	public void setList(List<JSONObject> openTasks, List<JSONObject> crossedTasks){
		
		mOpenTasks = openTasks;
		mCrossedTasks = crossedTasks;
	}
	
	@Override
	public int getViewTypeCount()
	{
		return 4;
	}
	
	@Override
	public int getItemViewType (int position)
	{
		if (isOpenTaskPosition(position)){
			return OPEN_TASK_TYPE;
		}
		else if (isAddTaskPosition(position)){
			return ADD_TASK_TYPE;
		}
		else if (isCrossLabelPosition(position)){
			return CROSS_TASKS_LABEL_TYPE;
		}
		else if (isCrossTaskPosition(position)){
			return CROSS_TASK_TYPE;
		}
		
		ClientLog.e(LOG_TAG, "ERROR! can't find type for position "+position+
				" while mOpenTasks.size()="+mOpenTasks.size()+" mCrossedTasks.size()="+mCrossedTasks.size());
		return -1;
	}
	
	private boolean isCrossTaskPosition(int position) {
		return position >= mOpenTasks.size()+2;
	}

	private boolean isCrossLabelPosition(int position) {
		return position == mOpenTasks.size()+1;
	}

	private boolean isAddTaskPosition(int position) {
		return position == mOpenTasks.size();
	}

	private boolean isOpenTaskPosition(int position) {
		
		if (position < mOpenTasks.size()){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int getCount() {
		
		int result = 0;
		result += mOpenTasks.size();
		result += mCrossedTasks.size();
		
		//for the add item
		result++;
		
		if (mCrossedTasks.size() > 0){
			//for the crossed label
			result++;
		}
		
		return result;
	}

	@Override
	public Object getItem(int position) {

		try {
			if (getItemViewType(position) == OPEN_TASK_TYPE){
				
				return mOpenTasks.get(position);
			}
			else if (getItemViewType(position) == CROSS_TASK_TYPE){
				
				int crossPosition = position-mOpenTasks.size()-2;
				return mCrossedTasks.get(crossPosition);
			}


		} catch (Exception e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		
		ClientLog.e(LOG_TAG, "ERROR!! no item to return in position "+position);

		return null;
	}

	@Override
	public long getItemId(int position) {
		return getItemViewType(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		TaskHolder holder;
		int type = getItemViewType(position);
		try{
			switch (type) {
			
			case ADD_TASK_TYPE:{
				convertView = getActivity().getLayoutInflater().inflate(R.layout.add_new_task_item, null);
				return convertView;
			}
			case OPEN_TASK_TYPE:{
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
				
				populateView(position, holder);
			}
			case CROSS_TASK_TYPE:{
				if (convertView == null)
				{
					convertView = getActivity().getLayoutInflater().inflate(R.layout.crossed_task_list_item, null);
					holder = new TaskHolder();
					holder.taskName = (TextView)convertView.findViewById(R.id.crossedTaskName);
					holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					convertView.setTag(holder);
				}
				else
				{
					holder = (TaskHolder)convertView.getTag();
				}
				
				populateView(position, holder);
			}
			case CROSS_TASKS_LABEL_TYPE:{
				if (convertView == null)
				{
					convertView = getActivity().getLayoutInflater().inflate(R.layout.crossed_list_label, null);
				}
			}
		}
		}
		catch (Exception e){
			ClientLog.e(LOG_TAG, "Error in position "+position+" type is "+type+
					" there are "+mOpenTasks.size()+" open tasks and "+mCrossedTasks.size()+" crossed ones", e);
		}
		
		if (convertView == null){
			ClientLog.e(LOG_TAG, "ERROR!! convert view is null for position "+position+" with type "+type);
		}
		
		return convertView;
		
	}

	private void populateView(int position, TaskHolder holder) {
		try {
			if (getItem(position) instanceof JSONObject){
				JSONObject jsonObj = (JSONObject)getItem(position);
				holder.taskName.setText(jsonObj.getString("content"));
			}
			
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
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
