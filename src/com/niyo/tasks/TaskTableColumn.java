package com.niyo.tasks;

import android.provider.BaseColumns;

public class TaskTableColumn implements BaseColumns {

	public static final String ELEMENT_CONTENT = "content";
	public static final String ELEMENT_CATEGORY = "category";
	public static final String ELEMENT_DONE = "done";
	public static final String ELEMENT_DATE = "date";
	
	public static final int COLUMN_CONTENT_INDEX = 1;
	public static final int COLUMN_CATEGORY_INDEX = 2;
	public static final int COLUMN_DONE_INDEX = 3;
	public static final int COLUMN_DATE_INDEX = 4;
}
