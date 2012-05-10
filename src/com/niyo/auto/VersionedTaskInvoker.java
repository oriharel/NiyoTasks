package com.niyo.auto;

import android.content.Context;
import android.os.Build;

import com.niyo.ServiceCaller;

public abstract class VersionedTaskInvoker {
	
	public abstract void invokeTask();
	
	public static VersionedTaskInvoker newInstance(Context context, ServiceCaller caller) {
	    final int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
	    VersionedTaskInvoker invoker = null;
	    
	    if (sdkVersion <= Build.VERSION_CODES.GINGERBREAD_MR1) {
	    	
	    	invoker = new GingerbreadTaskInvoker(context, caller);
	    	
	    } else {
	    	invoker = new ICSTaskInvoker(context, caller);
	    }


	    return invoker;
	}

}
