package com.niyo;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

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

}
