package com.niyo.data;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import com.niyo.ClientLog;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

public class GetJSONTask extends AsyncTask<URL, Void, String> {

	private static final String LOG_TAG = GetJSONTask.class.getSimpleName();
	private Context mApp;
	
	public GetJSONTask(Context app){
		mApp = app;
	}
	
	@Override
	protected String doInBackground(URL... params) {
		URL url = params[0];
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url.toString());
		try {
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			String json = readString(is);
			ClientLog.d(LOG_TAG, "json of url "+url+" is "+json);
			ContentValues values = new ContentValues();
			values.put(JSONTableColumns.ELEMENT_URL, url.getPath());
			values.put(JSONTableColumns.ELEMENT_JSON, json);
			
			Uri uri = Uri.parse(NiyoContentProvider.AUTHORITY+url.getPath());
			ClientLog.d(LOG_TAG, "uri is "+uri);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				mApp.getContentResolver().insert(uri, values);
			}
			else{
				ClientLog.w(LOG_TAG, "received "+response.getStatusLine().getStatusCode()+" for url "+url);
			}
			
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String readString(InputStream inputStream) {
		int BUFFER_SIZE = 20000;
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		ByteArrayBuffer baf = new ByteArrayBuffer(BUFFER_SIZE);
		byte[] buffer = new byte[BUFFER_SIZE];
		int current = 0;

		try {
			while ((current = bis.read(buffer)) != -1) 
			{
				baf.append(buffer, 0, current);
			}
		} catch (IOException e) 
		{
			ClientLog.e(LOG_TAG, "Error", e);
		}

		return new String(baf.toByteArray());
	}

}
