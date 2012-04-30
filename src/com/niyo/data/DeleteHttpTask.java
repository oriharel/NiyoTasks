package com.niyo.data;

import java.io.IOException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.niyo.ClientLog;

public class DeleteHttpTask extends AsyncTask<URL, Void, Integer> {
	
	private static final String LOG_TAG = DeleteHttpTask.class.getSimpleName();
	private Context mContext;
	
	public DeleteHttpTask(Context context){
		setContext(context);
	}

	
	@Override
	protected Integer doInBackground(URL... params) {
		URL url = params[0];
		DefaultHttpClient client = new DefaultHttpClient();
		HttpDelete delete = new HttpDelete(url.toString());
		Integer result = null;
//		try {
//			
//			HttpResponse response = client.execute(delete);
//			result = response.getStatusLine().getStatusCode();
//			ClientLog.d(LOG_TAG, "result is "+response.getStatusLine());
//			if (result == HttpStatus.SC_OK){
//				Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+url.getPath());
//			}
//			
//		} catch (ClientProtocolException e) {
//			ClientLog.e(LOG_TAG, "Error!", e);
//		} catch (IOException e) {
//			ClientLog.e(LOG_TAG, "Error!", e);
//		}
		
		return result;
	}
	public Context getContext() {
		return mContext;
	}
	public void setContext(Context context) {
		mContext = context;
	}

}
