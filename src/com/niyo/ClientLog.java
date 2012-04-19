package com.niyo;

import android.util.Log;

public class ClientLog
{
	private NiyoApplication _app;
	
	private static ClientLog _instance;
	
	public void init(NiyoApplication app)
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
		if (NiyoApplication.isLogEnabled()) 
		{
			result = Log.v(logTag, message, e);
		}
		else
		{
			result = -1;
		}
		
		return result;
	}

	public static int v(String logTag, String message)
	{
		int result;
		if (NiyoApplication.isLogEnabled()) 
		{
			result = Log.v(logTag, message);
		}
		else
		{
			result = -1;
		}
		
		return result;
	}

	public static int d(String logTag, String message, Exception e)
	{
		int result;
		if (NiyoApplication.isLogEnabled()) 
		{
			result = Log.d(logTag, message, e);
		}
		else
		{
			result = -1;
		}
		
		return result;
	}

	public static int d(String logTag, String message)
	{
		int result;
		
		if (NiyoApplication.isLogEnabled()) 
		{
			result = Log.d(logTag, message);
		}
		else
		{
			return -1;
		}
		
		return result;
	}

	public static int i(String logTag, String message, Exception e)
	{
		int result;
		if (NiyoApplication.isLogEnabled()) 
		{
			result = Log.i(logTag, message, e);
		}
		else
		{
			result = -1;
		}
		
		return result;
	}

	public static int i(String logTag, String message)
	{
		int result;
		if (NiyoApplication.isLogEnabled()) 
		{
			result = Log.i(logTag, message);
		}
		else
		{
			result = -1;
		}
		
		return result;
	}

	public static int w(String logTag, String message, Exception e)
	{
		int result;
		if (NiyoApplication.isLogEnabled()) 
		{
			result = Log.w(logTag, message, e);
		}
		else
		{
			result = -1;
		}
		
		return result;
	}

	public static int w(String logTag, String message)
	{
		int result;
		if (NiyoApplication.isLogEnabled()) 
		{
			result = Log.w(logTag, message);
		}
		else
		{
			result = -1;
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

	public NiyoApplication getApp() {
		return _app;
	}

	public void setApp(NiyoApplication app) {
		_app = app;
	}


}
