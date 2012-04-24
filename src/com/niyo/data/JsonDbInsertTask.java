package com.niyo.data;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

public class JsonDbInsertTask extends AsyncTask<String, Void, Boolean> {

	private Context mContext;
	
	public JsonDbInsertTask(Context context){
		
		mContext = context;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {

		Uri uri = Uri.parse(params[0]);
		String jsonStr = params[1];
		
		ContentValues values = new ContentValues();
		values.put(JSONTableColumns.ELEMENT_URL, "/"+uri.getLastPathSegment());
		values.put(JSONTableColumns.ELEMENT_JSON, jsonStr);
		
		mContext.getContentResolver().insert(uri, values);
		
		return null;
	}

}
