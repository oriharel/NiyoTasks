package com.niyo;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;

public class NiyoAbstractActivity extends Activity {
	protected final Handler pHandler = new Handler();
	
	protected Handler getHandler() {
		return pHandler;
	}
	
//	public Application getMyApplication(){
//		return getApplication();
//	}
}
