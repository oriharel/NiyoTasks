package com.niyo.auto;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.Time;

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
		
		ClientLog.d(LOG_TAG, "doInBackground started");
//		String[] calProjection = new String[] { "_id", "name" };
//		Uri calendars = Uri.parse("content://com.android.calendar/calendars");
		     
//		Cursor calCursor = mContext.getContentResolver().query(calendars, calProjection, null, null, null);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, 1);
		long toTest = cal.getTimeInMillis();
		Boolean foundEvent = false;
		String nextEventTitle = null;
		String eventStartTime = null;
		String eventLocation = null;
		
		Time now = new Time();
		now.setToNow();
		
		String selection = "dtstart <"+toTest+" and dtstart >"+now.toMillis(false);
		
		
//		if (calCursor != null && calCursor.moveToFirst()) {
			
//			 String calId; 
//			 int idColumn = calCursor.getColumnIndex("_id");
//			 do {
//			    calId = calCursor.getString(idColumn);
			    
			    String[] eventProjection = new String[]{"title", "dtstart", "eventLocation"};
			    Uri eventsUri = Uri.parse("content://com.android.calendar/events");
			    Cursor eventCursor = mContext.getContentResolver().query(eventsUri, eventProjection, selection, null, null);
			    
			    if (eventCursor.moveToFirst()){
			    	
			    	
			    	int titleColumn = eventCursor.getColumnIndex("title");
			    	int startColumn = eventCursor.getColumnIndex("dtstart");
			    	int locationColumn = eventCursor.getColumnIndex("eventLocation");
			    	do{
			    		nextEventTitle = eventCursor.getString(titleColumn);
			    		eventStartTime = eventCursor.getString(startColumn);
			    		eventLocation = eventCursor.getString(locationColumn);
			    		
			    		Long startTime = new Long(eventStartTime);
			    		
			    		if (startTime < toTest){
			    			toTest = startTime;
			    			foundEvent = true;
			    		}
			    	}while(eventCursor.moveToNext());
			    }
			    
//			 } while (calCursor.moveToNext());
//			}
		
		if (foundEvent){
			AutoEvent event = new AutoEvent();
			event.setTitle(nextEventTitle);
			event.setStartTime(new Long(eventStartTime));
			
			if (!TextUtils.isEmpty(eventLocation)){
				
				event.setLocation(eventLocation);
				Geocoder coder = new Geocoder(mContext);
				try {
					List<Address> addresses = coder.getFromLocationName(eventLocation, 1);
					
					if (addresses != null && addresses.size() > 0){
						
						Double lat = addresses.get(0).getLatitude();
						Double lon = addresses.get(0).getLongitude();
						event.setLat(lat.toString());
						event.setLon(lon.toString());
					}
				} catch (IOException e) {
					ClientLog.e(LOG_TAG, "Error!", e);
				}
			}
			ClientLog.d(LOG_TAG, "location is "+eventLocation);
			
			return event;
		}
		
		
		eventCursor.close();
		
		return null;
	}
	
	private void printColumns() {

		Cursor cursor = mContext.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"),
				new String[] { "_id", "displayName" }, "selected=1", null, null);
		if (cursor != null && cursor.moveToFirst()) {
			String[] calNames = new String[cursor.getCount()];
			int[] calIds = new int[cursor.getCount()];
			for (int i = 0; i < calNames.length; i++) {
				// retrieve the calendar names and ids
				// at this stage you can print out the display names to get an idea of what calendars the user has
				calIds[i] = cursor.getInt(0);
				calNames[i] = cursor.getString(1);
				ClientLog.d(LOG_TAG, "display name: "+calNames[i]);
				cursor.moveToNext();
			}
			cursor.close();
			if (calIds.length > 0) {
				// we're safe here to do any further work
			}
		}
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
