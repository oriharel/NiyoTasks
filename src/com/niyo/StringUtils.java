package com.niyo;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
	
	public static List<String> toList(String[] strArray)
	{
		List<String> result = new ArrayList<String>();
		
		for (String string : strArray) {
			result.add(string);
		}
		
		return result;
	}
}
