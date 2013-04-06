package com.niyo;

import java.util.HashSet;
import java.util.Set;

import com.niyo.auto.AppLauncher;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {

	public static final String PREFERENCES_FILE_NAME = "settings_data";
	private static final String LOG_TAG = SettingsManager.class.getSimpleName();
	
	public static void resetValues(Context context, String key) {
		SettingsManager.setInt(context, key, 0);
	}


	public static String getString(Context context, String key) 
	{
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		String value = settings.getString(key, null);
		return value;
	}
	
	public static Set<String> getStringSet(Context context, String setKey) 
	{
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		Set<String> value = settings.getStringSet(setKey, null);
		return value;
	}

	public static int getInt(Context context, String key, int defaultValue) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		//    settings.edit().
		int value = settings.getInt(key, defaultValue);
		return value;
	}
	
	public static float getFloat(Context context, String key, float defaultValue){
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		float value = settings.getFloat(key, defaultValue);
		return value;
	}

	public static long getLong(Context context, String key, long defaultValue) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		long value = settings.getLong(key, defaultValue);
		return value;
	}

	public static boolean getBoolean(Context context, String key, boolean defaultValue) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		boolean value = settings.getBoolean(key, defaultValue);
		return value;
	}

	public static void setBoolean(Context context, String key, boolean value) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static void setString(Context context, String key, String value) 
	{
		if (value == null)
		{
			removeKey(context, key);
			return;
		}
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void addToStringSet(Context context, String setKey, String key, String value) 
	{
		ClientLog.d(LOG_TAG, "adding "+value+" to "+setKey+" set");
		if (value == null)
		{
			removeKey(context, key);
			return;
		}
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		Set<String> set = settings.getStringSet(setKey, null);
		if (set == null) {
			set = new HashSet<String>();
		}
		set.add(value);
		
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(setKey);
		editor.commit();
		editor.putStringSet(setKey, set);
		editor.commit();
	}

	public static void setInt(Context context, String key, int value) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public static void setFloat(Context context, String key, float value) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	public static void setLong(Context context, String key, long value) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public static void removeKey(Context context, String key)
	{
		SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(key);
		editor.commit();
	}
	
}
