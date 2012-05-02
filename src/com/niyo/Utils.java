package com.niyo;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utils {

	public static Comparator<JSONObject> getTaskJSONComparator() {

		Comparator<JSONObject> result = new Comparator<JSONObject>() {

			@Override
			public int compare(JSONObject lTask, JSONObject rTask) {

				String lContent = "";
				String rContent = "";
				try {
					
					lContent = lTask.getString("content");
					rContent = rTask.getString("content");
					
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return lContent.compareTo(rContent);
			}
		};

		return result;
	}
	
	public static void hideKeyboard(Context context, View view) 
	{
		IBinder binder = view.getWindowToken();
		InputMethodManager service = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		service.hideSoftInputFromWindow(binder, 0);
	}
	
	public static void showKeyboard(Context context, View view) {
		IBinder binder = view.getWindowToken();
		InputMethodManager service = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		service.showSoftInputFromInputMethod(binder, InputMethodManager.SHOW_FORCED);
	}
	
	public static void showKeyboard2(Context context) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY); 
	}

}
