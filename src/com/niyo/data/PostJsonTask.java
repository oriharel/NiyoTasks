package com.niyo.data;

import java.io.IOException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.niyo.ClientLog;

public class PostJsonTask extends AsyncTask<URL, Void, Integer> {

	private static final String LOG_TAG = PostJsonTask.class.getSimpleName();
	private StringEntity mEntity;
	private Context mContext;
	
	public PostJsonTask(StringEntity entity, Context context)
	{
		setEntity(entity);
		setContext(context);
	}
	
	@Override
	protected Integer doInBackground(URL... params) {
		
		URL url = params[0];
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url.toString());
		post.setEntity(getEntity());
		try {
			ClientLog.d(LOG_TAG, "entity is "+GetJSONTask.readString(getEntity().getContent()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Integer result = null;
		try {
			
			HttpResponse response = client.execute(post);
			result = response.getStatusLine().getStatusCode();
			ClientLog.d(LOG_TAG, "result is "+response.getStatusLine());
			if (result == HttpStatus.SC_OK){
				Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+url.getPath());
				getContext().getContentResolver().update(uri, null, null, null);
			}
			
		} catch (ClientProtocolException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		} catch (IOException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		
		return result;
	}

	public StringEntity getEntity() {
		return mEntity;
	}

	public void setEntity(StringEntity entity) {
		mEntity = entity;
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context context) {
		mContext = context;
	}

}
