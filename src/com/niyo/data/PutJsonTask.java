package com.niyo.data;

import java.io.IOException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.niyo.ClientLog;
import com.niyo.ServiceCaller;

public class PutJsonTask extends AsyncTask<URL, Void, Integer> {

	private static final String LOG_TAG = PutJsonTask.class.getSimpleName();
	private HttpEntity mEntity;
	private Context mContext;
	private ServiceCaller mCaller;
	private Boolean mShouldUpdate = false;
	public PutJsonTask(HttpEntity entity, Context context)
	{
		setEntity(entity);
		setContext(context);
	}
	
	public PutJsonTask(HttpEntity entity, Context context, ServiceCaller caller)
	{
		this(entity, context);
		mCaller = caller;
	}
	
	@Override
	protected Integer doInBackground(URL... params) {
		
		URL url = params[0];
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPut put = new HttpPut(url.toString());
		put.setEntity(getEntity());
		try {
			ClientLog.d(LOG_TAG, "entity is "+GetJSONTask.readString(getEntity().getContent()));
		} catch (IOException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		Integer result = null;
		try {
			
			HttpResponse response = client.execute(put);
			result = response.getStatusLine().getStatusCode();
			ClientLog.d(LOG_TAG, "result is "+response.getStatusLine());
			if (result == HttpStatus.SC_OK){
				Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+url.getPath());
				
				if (getShouldUpdate()){
					getContext().getContentResolver().update(uri, null, null, null);
				}
				
			}
			
		} catch (ClientProtocolException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		} catch (IOException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		
		return result;
	}

	public HttpEntity getEntity() {
		return mEntity;
	}

	public void setEntity(HttpEntity entity) {
		mEntity = entity;
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context context) {
		mContext = context;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		
		if (mCaller != null){
			if (result == HttpStatus.SC_OK){
	        	mCaller.success(result);
	        }
	        else{
	        	mCaller.failure(result, "failed");
	        }
		}
    }

	public Boolean getShouldUpdate() {
		return mShouldUpdate;
	}

	public void setShouldUpdate(Boolean shouldUpdate) {
		mShouldUpdate = shouldUpdate;
	} 

}
