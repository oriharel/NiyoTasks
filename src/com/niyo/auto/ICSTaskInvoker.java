package com.niyo.auto;

import com.niyo.ServiceCaller;

import android.content.Context;

public class ICSTaskInvoker extends VersionedTaskInvoker {
	
	private Context mContext;
	private ServiceCaller mCaller;
	
	public ICSTaskInvoker(Context context, ServiceCaller caller){
		mContext = context;
		mCaller = caller;
	}

	@Override
	public void invokeTask() {
		
		NextEventFetchTaskICS task = new NextEventFetchTaskICS(mContext, mCaller);
		task.execute((Void[])null);

	}

}
