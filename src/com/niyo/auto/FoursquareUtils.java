package com.niyo.auto;

import org.json.JSONException;
import org.json.JSONObject;

import com.niyo.ClientLog;

public class FoursquareUtils {

	private static final String LOG_TAG = FoursquareUtils.class.getSimpleName();
	
	public static int getFoursqaureStatus(String response) {
		try {
			JSONObject json;

			json = new JSONObject(response);


			if (json.has("meta")){
				String code = json.getJSONObject("meta").getString("code");
				return new Integer(code);
			}
		} catch (JSONException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}

		ClientLog.e(LOG_TAG, "couldn't read meta");
		return -1;
	}
}
