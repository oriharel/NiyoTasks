package com.niyo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.niyo.ClientLog;

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
		String id = values.getAsString(FriendsTableColumns._ID);
//		String insertedJson = values.getAsString(JSONTableColumns.ELEMENT_JSON);
//		ClientLog.d(LOG_TAG, "insertedUrl is "+insertedUrl);
//		ClientLog.d(LOG_TAG, "insertedJson is "+insertedJson);
		
//		boolean isEqual = false;
//		Cursor cursor = retrieveFromJsonTable(uri, null, null, null);
		
//		if (cursor.moveToFirst()) 
//		{
//			String storedJson = cursor.getString(JSONTableColumns.COLUMN_JSON_INDEX);
//			isEqual = storedJson == null ? false : storedJson.equals(insertedJson);
//		}
//		else
//		{
//			ClientLog.d(LOG_TAG, "uri "+uri+" cursor is empty");
//		}
//		cursor.close();
		
//		if (!isEqual)
//		{
//			ClientLog.d(LOG_TAG, "inserting uri "+uri);
			String table = NiyoDbHelper.FRIENDS_TABLE;
			getWritableDb().delete(table, FriendsTableColumns._ID + 
					"='" + id + "'", null);
			getWritableDb().insert(table, FriendsTableColumns._ID, values);
			getContext().getContentResolver().notifyChange(uri, null);
//		}
//		else
//		{
//			ClientLog.d(LOG_TAG, "received no change in data");
//		}
		
		return uri;
	}
	
//	private Cursor retrieveFromJsonTable(Uri uri, String selection, String[] selectionArgs, String sort) 
//	{
//		ClientLog.d(LOG_TAG, "retrieve started with "+uri);
//		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
//		qb.setTables(NiyoDbHelper.FRIENDS_TABLE);
//		selection = JSONTableColumns.ELEMENT_URL + "='/" + uri.getLastPathSegment() + "'";
//		ClientLog.d(LOG_TAG, "going to query with selection "+selection);
//	    String orderBy = FriendsTableColumns._ID;
//	    Cursor cursor = qb.query(getReadableDb(), null, selection, selectionArgs, null, null, orderBy);
	    
//	    return cursor;
//	}
	
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
		
		qb.setTables(NiyoDbHelper.FRIENDS_TABLE);
		
		ClientLog.d(LOG_TAG, "going to query with selection "+selection);
		String orderBy = FriendsTableColumns._ID;
		
		Cursor cursor = qb.query(getReadableDb(), projection, selection, selectionArgs, null, null, orderBy);
		
		ClientLog.d(LOG_TAG, "got " + cursor.getCount()
				+ " results from uri " + uri);
		
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		return 0;
	}

}
