package com.niyo.auto;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import com.niyo.ClientLog;
import com.niyo.ServiceCaller;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.Time;

public class NextEventFetchTaskICS extends AsyncTask<Void, Void, AutoEvent> {

	private static final String LOG_TAG = NextEventFetchTaskICS.class.getSimpleName();
	private Context mContext;
	private ServiceCaller mCaller;
	
	public NextEventFetchTaskICS(Context context, ServiceCaller caller){
		
		mContext = context;
		mCaller = caller;
	}
	@Override
	protected AutoEvent doInBackground(Void... params) {
		
		
		Uri uri = CalendarContract.Events.CONTENT_URI;
		String[] projection = new String[] {
				CalendarContract.Events.TITLE,
				CalendarContract.Events.DTSTART,
				CalendarContract.Events.EVENT_LOCATION
		};
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, 1);
		long toTest = cal.getTimeInMillis();
		
		Time now = new Time();
		now.setToNow();
		
		String selection = CalendarContract.Events.DTSTART +"<"+toTest+" and "+CalendarContract.Events.DTSTART +">"+now.toMillis(false);
		Cursor calendarCursor = mContext.getContentResolver().query(uri, projection, selection, null, null);
		String eventTitle = "";
		String locationString = "";
		calendarCursor.moveToFirst();
		
		boolean foundEvent = false;
		while (!calendarCursor.isAfterLast())
		{
			String eventTime = calendarCursor.getString(calendarCursor.getColumnIndex(CalendarContract.Events.DTSTART));
			
			Long eventTimeLong = new Long(eventTime);
			
			if (eventTimeLong < toTest){
				toTest = eventTimeLong;
				eventTitle = calendarCursor.getString(calendarCursor.getColumnIndex(CalendarContract.Events.TITLE));
				
				if (!calendarCursor.isNull(calendarCursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION))){
					
					locationString = calendarCursor.getString(calendarCursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION));
					foundEvent = true;
				}
				
			}
			calendarCursor.moveToNext();
		}
		
		if (foundEvent){
			
			AutoEvent result = new AutoEvent();
			result.setTitle(eventTitle);
			result.setStartTime(toTest);
			
			if (!TextUtils.isEmpty(locationString)){
				
				Geocoder coder = new Geocoder(mContext);
				try {
					List<Address> addresses = coder.getFromLocationName(locationString, 1);
					
					if (addresses != null && addresses.size() > 0){
						
						Double lat = addresses.get(0).getLatitude();
						Double lon = addresses.get(0).getLongitude();
						result.setLat(lat.toString());
						result.setLon(lon.toString());
					}
				} catch (IOException e) {
					ClientLog.e(LOG_TAG, "Error!", e);
				}
			}
			
			return result;
		}
		else{
			return null;
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
