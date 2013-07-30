package com.niyo.data;

import android.provider.BaseColumns;

public class FriendsTableColumns implements BaseColumns {
	public static final String NAME = "name";
	public static final String GIVEN_NAME = "given_name";
	public static final String FAMILY_NAME = "family_name";
	public static final String LINK = "link";
	public static final String PICTURE_URL = "picture";
	public static final String GENDER = "gender";
	public static final String BIRTHDAY = "birthday";
	public static final String LATITUDE = "lat";
	public static final String LONGITUDE = "lon";
	public static final String LAST_LOCATION_TIMESTAMP = "location_time";
	
	public static final int COLUMN_NAME_INDEX = 1;
	public static final int COLUMN_GIVEN_NAME_INDEX = 2;
	public static final int COLUMN_FAMILY_NAME_INDEX = 3;
	public static final int COLUMN_LINK_INDEX = 4;
	public static final int COLUMN_PICTURE_URL_INDEX = 5;
	public static final int COLUMN_GENDER_INDEX = 6;
	public static final int COLUMN_BIRTHDAY_INDEX = 7;
	public static final int COLUMN_LATITUDE_INDEX = 8;
	public static final int COLUMN_LONGITUDE_INDEX = 9;
	public static final int COLUMN_LAST_LOCATION_INDEX = 9;

}
