package com.niyo.logs;

import android.content.Context;
import android.os.AsyncTask;

public class LogCollectorTask extends AsyncTask<Void, Void, Boolean> {

	private Context mContext;
	private String mMsg;
	
	public LogCollectorTask(Context context, String msg){
		
		mContext = context;
		mMsg = msg;
	}
	@Override
	protected Boolean doInBackground(Void... params) {
		
		
		
		
		
		return true;
	}
	
	@Override
    protected void onPostExecute(Boolean result) 
	{
    }

}
