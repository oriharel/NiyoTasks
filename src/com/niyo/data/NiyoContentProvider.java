package com.niyo.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.niyo.ClientLog;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class NiyoContentProvider extends ContentProvider {

	private static String LOG_TAG = NiyoContentProvider.class.getSimpleName();
	public static String AUTHORITY = "content://com.niyo.provider";
	private NiyoDbHelper _dbHelper;
	private static final String DATABASE_NAME = "niyo.db";
	private static final int DATABASE_VERSION = 1;
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		ClientLog.d(LOG_TAG, "insert started "+uri);
		String insertedUrl = values.getAsString(JSONTableColumns.ELEMENT_URL);
		String insertedJson = values.getAsString(JSONTableColumns.ELEMENT_JSON);
		ClientLog.d(LOG_TAG, "insertedUrl is "+insertedUrl);
		
		boolean isEqual = false;
		Cursor cursor = retrieveFromJsonTable(uri, null, null, null);
		
		if (cursor.moveToFirst()) 
		{
			String storedJson = cursor.getString(JSONTableColumns.COLUMN_JSON_INDEX);
			isEqual = storedJson == null ? false : storedJson.equals(insertedJson);
		}
		else
		{
			ClientLog.d(LOG_TAG, "uri "+uri+" cursor is empty");
		}
		cursor.close();
		
		if (!isEqual)
		{
			ClientLog.d(LOG_TAG, "inserting uri "+uri);
			String table = NiyoDbHelper.JSON_TABLE;
			getWritableDb().delete(table, JSONTableColumns.ELEMENT_URL + 
					"='" + insertedUrl + "'", null);
			getWritableDb().insert(table, JSONTableColumns.ELEMENT_JSON, values);
			getContext().getContentResolver().notifyChange(uri, null);
		}
		else
		{
			ClientLog.d(LOG_TAG, "received no change in data");
		}
		
		return uri;
	}
	
	private Cursor retrieveFromJsonTable(Uri uri, String selection, String[] selectionArgs, String sort) 
	{
		ClientLog.d(LOG_TAG, "retrieve started with "+uri);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(NiyoDbHelper.JSON_TABLE);
		selection = JSONTableColumns.ELEMENT_URL + "='/" + uri.getLastPathSegment() + "'";
		ClientLog.d(LOG_TAG, "going to query with selection "+selection);
	    String orderBy = JSONTableColumns._ID;
	    Cursor cursor = qb.query(getReadableDb(), null, selection, selectionArgs, null, null, orderBy);
	    
	    return cursor;
	}
	
	/**
	   * ********************************************************
	   * Getters and Setters.
	   * *********************************************************
	   */
	  private SQLiteDatabase getWritableDb() 
	  {
	    return getDbHelper().getWritableDatabase();
	  }

	  private SQLiteDatabase getReadableDb() {
	    return getDbHelper().getReadableDatabase();
	  }

	  private NiyoDbHelper getDbHelper() {
	    return _dbHelper;
	  }

	  private void setDbHelper(NiyoDbHelper dbHelper) {
	    _dbHelper = dbHelper;
	  }

	@Override
	public boolean onCreate() {
		ClientLog.d(LOG_TAG, "onCreate started");
		Context context = getContext();

		setDbHelper(new NiyoDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION));
		return getWritableDb() == null ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		ClientLog.d(LOG_TAG, "query started with "+uri);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		qb.setTables(NiyoDbHelper.JSON_TABLE);
		
		ClientLog.d(LOG_TAG, "going to query with selection "+selection);
		String orderBy = JSONTableColumns._ID;
		
		Cursor cursor = qb.query(getReadableDb(), projection, selection, selectionArgs, null, null, orderBy);
		
		ClientLog.d(LOG_TAG, "got " + cursor.getCount()
				+ " results from uri " + uri);
		
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		ArrayList<String> urls = new ArrayList<String>();
		try {
			urls.add("http://niyoapi.appspot.com/categories");
			urls.add("http://niyoapi.appspot.com/tasks");
			
			Intent intent = new Intent(getContext(), JsonFetchIntentService.class);
			intent.putStringArrayListExtra(JsonFetchIntentService.URLS_EXTRA, urls);
			getContext().startService(intent);
			
		} catch (Exception e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		return 0;
	}

}
