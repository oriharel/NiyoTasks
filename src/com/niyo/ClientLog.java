package com.niyo;

import android.app.Application;
import android.util.Log;

public class ClientLog
{
	private Application _app;
	
	private static ClientLog _instance;
	
	public void init(Application app)
	{
		setApp(app);
	}
	
	private ClientLog()
	{
		
	}
	
	public static ClientLog getInstance()
	{
		synchronized (ClientLog.class) 
		{
			ClientLog inst = _instance;
			if (inst == null)
			{
				synchronized (ClientLog.class) 
				{
					_instance = new ClientLog();
				}
			}
		}
		
		return _instance;
	}
	
	
	public static int v(String logTag, String message, Exception e)
	{
		int result;
//		if (Application.isLogEnabled()) 
		{
			result = Log.v(logTag, message, e);
		}
//		else
		{
//			result = -1;
		}
		
		return result;
	}

	public static int v(String logTag, String message)
	{
		int result;
//		if (Application.isLogEnabled()) 
		{
			result = Log.v(logTag, message);
		}
//		else
		{
//			result = -1;
		}
		
		return result;
	}

	public static int d(String logTag, String message, Exception e)
	{
		int result;
//		if (Application.isLogEnabled()) 
		{
			result = Log.d(logTag, message, e);
		}
//		else
		{
//			result = -1;
		}
		
		return result;
	}

	public static int d(String logTag, String message)
	{
		int result;
		
//		if (Application.isLogEnabled()) 
		{
			result = Log.d(logTag, message);
		}
//		else
		{
//			return -1;
		}
		
		return result;
	}

	public static int i(String logTag, String message, Exception e)
	{
		int result;
//		if (Application.isLogEnabled()) 
		{
			result = Log.i(logTag, message, e);
		}
//		else
		{
//			result = -1;
		}
		
		return result;
	}

	public static int i(String logTag, String message)
	{
		int result;
//		if (Application.isLogEnabled()) 
		{
			result = Log.i(logTag, message);
		}
//		else
		{
//			result = -1;
		}
		
		return result;
	}

	public static int w(String logTag, String message, Exception e)
	{
		int result;
//		if (Application.isLogEnabled()) 
		{
			result = Log.w(logTag, message, e);
		}
//		else
		{
//			result = -1;
		}
		
		return result;
	}

	public static int w(String logTag, String message)
	{
		int result;
//		if (Application.isLogEnabled()) 
		{
			result = Log.w(logTag, message);
		}
//		else
		{
//			result = -1;
		}
		
		return result;
	}

	public static int e(String logTag, String message, Throwable e)
	{
		int result;
		result = Log.e(logTag, message, e);
		
		return result;
	}

	public static int e(String logTag, String message)
	{
		int result;
		result = Log.e(logTag, message);
		
		return result;
	}

	public Application getApp() {
		return _app;
	}

	public void setApp(Application app) {
		_app = app;
	}


}
