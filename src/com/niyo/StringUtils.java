package com.niyo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.niyo.auto.AutoVenue;
import com.niyo.tasks.TaskJsonObject;

public class StringUtils {
	
	public static List<String> toList(String[] strArray)
	{
		List<String> result = new ArrayList<String>();
		
		for (String string : strArray) {
			result.add(string);
		}
		
		return result;
	}

	public static List<JSONObject> toList(JSONArray tasks) {
		
		List<JSONObject> result = new ArrayList<JSONObject>();
		
		for (int i = 0; i < tasks.length(); i++){
			try {
				result.add(tasks.getJSONObject(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	public static List<TaskJsonObject> toTasksList(JSONArray tasks) {
		
		List<TaskJsonObject> result = new ArrayList<TaskJsonObject>();
		
		for (int i = 0; i < tasks.length(); i++){
			try {
				TaskJsonObject obj = new TaskJsonObject(tasks.getJSONObject(i));
				result.add(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	

	public static String printList(List<JSONObject> tasks) {
		
		StringBuffer result = new StringBuffer();
		
		for (Object object : tasks) {
			result.append(object);
			result.append(", ");
		}
		
		return result.toString();
		
	}

	public static List<JSONObject> sort(List<JSONObject> list, Comparator<JSONObject> comparator) {
		
		JSONObject[] asArray = (JSONObject[])list.toArray(new JSONObject[list.size()]);
		Arrays.sort(asArray, comparator);
		return toList(asArray);
	}

	private static List<JSONObject> toList(JSONObject[] asArray) {
		
		List<JSONObject> result = new ArrayList<JSONObject>();
		
		for (JSONObject jsonObject : asArray) {
			result.add(jsonObject);
		}
		
		return result;
	}
	
	public static List<AutoVenue> toList(AutoVenue[] asArray) {
		
		List<AutoVenue> result = new ArrayList<AutoVenue>();
		
		for (AutoVenue jsonObject : asArray) {
			result.add(jsonObject);
		}
		
		return result;
	}

	public static String createCommaSeperatedList(Set<String> strings) 
	{
		StringBuffer result = new StringBuffer();
		
		if (strings == null){
			return null;
		}
		
		Iterator<String> iter = strings.iterator();
		int count = 0;
		while (iter.hasNext()){
			result.append(iter.next());
			if (count < strings.size()-1){
				result.append(",");
			}
		}
		
		return result.toString();
	}

	public static String printMap(HashMap<String, String> map) {
		
		StringBuffer result = new StringBuffer();
		
		for (String key : map.keySet()) {
			result.append("key="+key+" value="+map.get(key)+"\n");
		}
		return result.toString();
	}
}
