package com.niyo.data;

import com.niyo.ClientLog;
import com.niyo.categories.CategoryTableColumns;
import com.niyo.tasks.TaskTableColumn;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class NiyoDbHelper extends SQLiteOpenHelper {

	private static final String LOG_TAG = NiyoDbHelper.class.getSimpleName();
	public static final String FRIENDS_TABLE = "friends";
//	public static final String CATEGORIES_TABLE = "categories";
//	public static final String TASKS_TABLE = "tasks";
	
	private static final String TABLE_FRIENDS_CREATE =
			"create table " + FRIENDS_TABLE + " ("
					+ FriendsTableColumns._ID + " integer primary key autoincrement, "
					+ FriendsTableColumns.NAME + " TEXT, "
					+ FriendsTableColumns.GIVEN_NAME + " TEXT, "
					+ FriendsTableColumns.FAMILY_NAME + " TEXT, "
					+ FriendsTableColumns.LINK + " TEXT, "
					+ FriendsTableColumns.PICTURE_URL + " TEXT, "
					+ FriendsTableColumns.GENDER + " TEXT, "
					+ FriendsTableColumns.BIRTHDAY + " DATE, "
					+ FriendsTableColumns.LATITUDE + " DOUBLE(16,8), "
					+ FriendsTableColumns.LONGITUDE + " DOUBLE(16,8), "
					+ FriendsTableColumns.LAST_LOCATION_TIMESTAMP + " TIMESTAMP);";
	
//	private static final String TABLE_CATEGORIES_CREATE =
//			"create table " + CATEGORIES_TABLE + " ("
//					+ CategoryTableColumns._ID + " integer primary key autoincrement, "
//					+ CategoryTableColumns.ELEMENT_NAME + " TEXT, "
//					+ CategoryTableColumns.ELEMENT_DATE + " DATETIME);";
//	
//	private static final String TABLE_TASKS_CREATE =
//			"create table " + TASKS_TABLE + " ("
//			+ TaskTableColumn._ID + " integer primary key autoincrement, "
//			+ TaskTableColumn.ELEMENT_CONTENT + " TEXT, "
//			+ TaskTableColumn.ELEMENT_CATEGORY + " TEXT,"
//			+ TaskTableColumn.ELEMENT_DONE + " TEXT,"
//			+ TaskTableColumn.ELEMENT_DATE + " ELEMENT_DATE);";
	
	public NiyoDbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ClientLog.d(LOG_TAG, "onCreate started");
		db.execSQL(TABLE_FRIENDS_CREATE);
//		db.execSQL(TABLE_CATEGORIES_CREATE);
//		db.execSQL(TABLE_TASKS_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS_CREATE);
//		db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE);
//		db.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE);

	}

}
