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

public class CrossedTasksListAdapter extends BaseAdapter {

	private static final String LOG_TAG = CrossedTasksListAdapter.class.getSimpleName();
	private Activity mActivity;
	private List<JSONObject> mTasks;
	
	public CrossedTasksListAdapter(Activity activity){
		setActivity(activity);
	}
	
	public void setList(List<JSONObject> tasks){
		
		mTasks = tasks;
	}
	
	
	@Override
	public int getCount() {
		return mTasks.size();
	}

	@Override
	public JSONObject getItem(int position) {
		try {
			return (JSONObject)mTasks.get(position);
		} catch (Exception e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TaskHolder holder;
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

		try {
			holder.taskName.setText(getItem(position).getString("content"));
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}


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
