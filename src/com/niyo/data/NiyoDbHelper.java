package com.niyo.data;

import com.niyo.ClientLog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class NiyoDbHelper extends SQLiteOpenHelper {

	private static final String LOG_TAG = NiyoDbHelper.class.getSimpleName();
	public static final String JSON_TABLE = "jsons";
	
	private static final String TABLE_JSON_CREATE =
			"create table " + JSON_TABLE + " ("
			+ JSONTableColumns._ID + " integer primary key autoincrement, "
			+ JSONTableColumns.ELEMENT_URL + " TEXT, "
			+ JSONTableColumns.ELEMENT_JSON + " TEXT);";
	
	public NiyoDbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ClientLog.d(LOG_TAG, "onCreate started");
		db.execSQL(TABLE_JSON_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + JSON_TABLE);

	}

}
