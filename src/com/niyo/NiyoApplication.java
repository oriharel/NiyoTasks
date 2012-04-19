package com.niyo;

import android.app.Application;

public class NiyoApplication extends Application {

	private static Boolean s_logEnabled = true;
	public static boolean isLogEnabled() {
		return s_logEnabled;
	}

}
