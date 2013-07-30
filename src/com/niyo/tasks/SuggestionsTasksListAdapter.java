//package com.niyo.tasks;
//
//import java.util.List;
//
//import org.json.JSONException;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//
//import com.niyo.ClientLog;
//import com.niyo.R;
//
//public class SuggestionsTasksListAdapter extends ArrayAdapter<TaskJsonObject> {
//
//	private static final String LOG_TAG = SuggestionsTasksListAdapter.class.getSimpleName();
//	private AddTaskActivity mActivity;
//	
//	public SuggestionsTasksListAdapter(Context context, int resource,
//			int textViewResourceId, List<TaskJsonObject> objects) {
//		super(context, resource, textViewResourceId, objects);
//		setActivity((AddTaskActivity)context);
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		
//		convertView = super.getView(position, convertView, parent);
//		TaskHolder holder = (TaskHolder)convertView.getTag();
//		if (holder == null)
//		{
//			holder = new TaskHolder();
//			holder.taskName = (TextView)convertView.findViewById(R.id.taskSuggestionName);
//			convertView.setTag(holder);
//		}
//
//		try {
//			holder.taskName.setText(getItem(position).getString("content"));
//			holder.taskName.setTag(getItem(position).getString("taskId"));
//		} catch (JSONException e) {
//			ClientLog.e(LOG_TAG, "Error!", e);
//		}
//
//		return convertView;
//	}
//	
//	
//	public AddTaskActivity getActivity() {
//		return mActivity;
//	}
//	public void setActivity(AddTaskActivity activity) {
//		mActivity = activity;
//	}
//	
//	static class TaskHolder{
//		TextView taskName;
//	}
//	
//}
