package com.niyo.tasks;

import java.util.List;

import com.niyo.ServiceCaller;
import com.niyo.data.JSONTableColumns;

import android.content.Context;
import android.os.AsyncTask;

public class GetTasksFromProviderTask extends AsyncTask<Void, Void, List<TaskItem>> {

	private Context mContext;
	private ServiceCaller mCaller;
	
	private String[] mProjection = new String[] 
            {
				JSONTableColumns._ID,
				JSONTableColumns.ELEMENT_URL, 
				JSONTableColumns.ELEMENT_JSON, 
            };
	private String mTasksSelection = JSONTableColumns.ELEMENT_URL + "='/tasks'";
	
	public GetTasksFromProviderTask(Context context, ServiceCaller caller){
		mContext = context;
		mCaller = caller;
	}
	
	@Override
	protected List<TaskItem> doInBackground(Void... params) {
		return null;
	}

}
