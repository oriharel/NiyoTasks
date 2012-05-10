package com.niyo.auto;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.niyo.ClientLog;
import com.niyo.ServiceCaller;

public class NextEventFetchTaskGingerbread extends AsyncTask<Void, Void, AutoEvent> {

	private static final String LOG_TAG = NextEventFetchTaskGingerbread.class.getSimpleName();
	private Context mContext;
	private ServiceCaller mCaller;
	
	public NextEventFetchTaskGingerbread(Context context, ServiceCaller caller){
		
		mContext = context;
		mCaller = caller;
	}
	
	@Override
	protected AutoEvent doInBackground(Void... params) {
		
		String[] calProjection = new String[] { "_id", "name" };
		Uri calendars = Uri.parse("content://calendar/calendars");
		     
		Cursor calCursor = mContext.getContentResolver().query(calendars, calProjection, null, null, null);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, 1);
		long toTest = cal.getTimeInMillis();
		Boolean foundEvent = false;
		String nextEventTitle = null;
		String eventStartTime = null;
		
		
		if (calCursor != null && calCursor.moveToFirst()) {
			
			 String calId; 
			 int idColumn = calCursor.getColumnIndex("_id");
			 do {
			    calId = calCursor.getString(idColumn);
			    
			    String[] eventProjection = new String[]{"title", "dstart"};
			    Uri eventsUri = Uri.parse("content://calendar/events/"+calId);
			    Cursor eventCursor = mContext.getContentResolver().query(eventsUri, eventProjection, null, null, null);
			    
			    if (eventCursor.moveToFirst()){
			    	
			    	int titleColumn = eventCursor.getColumnIndex("title");
			    	int startColumn = eventCursor.getColumnIndex("dtstart");
			    	
			    	do{
			    		nextEventTitle = eventCursor.getString(titleColumn);
			    		eventStartTime = eventCursor.getString(startColumn);
			    		
			    		Long startTime = new Long(eventStartTime);
			    		
			    		if (startTime < toTest){
			    			toTest = startTime;
			    			foundEvent = true;
			    		}
			    	}while(eventCursor.moveToNext());
			    }
			    
			 } while (calCursor.moveToNext());
			}
		
		if (foundEvent){
			AutoEvent event = new AutoEvent();
			event.setTitle(nextEventTitle);
			event.setStartTime(new Long(eventStartTime));
			return event;
		}
		
		return null;
	}
	
	@Override
    protected void onPostExecute(AutoEvent data) 
	{
        if (isCancelled()) 
        {
        	ClientLog.d(LOG_TAG, "isCancelled activated");
        }
        
        ClientLog.d(LOG_TAG, "calling success");
        try 
        {
			mCaller.success(data);
		} 
        catch (Exception e) 
        {
			ClientLog.e(LOG_TAG, "Error! with ", e);
		}
    }

}
