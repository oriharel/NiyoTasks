package com.niyo.auto;

import android.content.Context;

import com.niyo.ServiceCaller;

public class GingerbreadTaskInvoker extends VersionedTaskInvoker {

	private ServiceCaller mCaller;
	private Context mContext;
	
	public GingerbreadTaskInvoker(Context context, ServiceCaller caller){
		mCaller = caller;
		mContext = context;
	}
	@Override
	public void invokeTask() {
		
		NextEventFetchTaskGingerbread task = new NextEventFetchTaskGingerbread(mContext, mCaller);
		task.execute((Void[])null);

	}

}
