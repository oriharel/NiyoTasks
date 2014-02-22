package com.niyo.auto;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import com.niyo.ClientLog;
import com.niyo.ServiceCaller;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Instances;
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
		
		
//		Uri uri = CalendarContract.Instances.CONTENT_URI;
//		Uri instancesUri = CalendarContract.Instances.CONTENT_URI;
		String[] projection = new String[] {
				Instances.EVENT_ID,
				Instances.BEGIN,
				Instances.TITLE,
				CalendarContract.Events.EVENT_LOCATION

		};
		
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.DAY_OF_WEEK, 1);
//		long toTest = cal.getTimeInMillis();
		
		Time now = new Time();
		now.setToNow();
		
		Calendar beginTime = Calendar.getInstance();
		long startMillis = beginTime.getTimeInMillis();
		
		Calendar endTime = Calendar.getInstance();
		endTime.add(Calendar.DAY_OF_WEEK, 1);
		long endMillis = endTime.getTimeInMillis();
		long toTest = endMillis;
		
		Cursor calendarCursor = null;
		ContentResolver cr = mContext.getContentResolver();
		
		Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
		ContentUris.appendId(builder, startMillis);
		ContentUris.appendId(builder, endMillis);
		
//		String selection = CalendarContract.Instances.BEGIN +"<"+toTest+" and "+CalendarContract.Instances.BEGIN +">"+now.toMillis(false);
		calendarCursor = cr.query(builder.build(), 
				projection, 
			    "", 
			    new String[0], 
			    null);
		String selectedEventTitle = "";
		String selectedLocationString = "";
		ClientLog.d(LOG_TAG, "events count is "+calendarCursor.getCount());
//		calendarCursor.moveToNext();
		
		boolean foundEvent = false;
		while (calendarCursor.moveToNext())
		{
			String eventTime = calendarCursor.getString(calendarCursor.getColumnIndex(Instances.BEGIN));
			String eventTitle = calendarCursor.getString(calendarCursor.getColumnIndex(Instances.TITLE));
			
			ClientLog.d(LOG_TAG, "event title "+eventTitle+" time is "+eventTime+" toTest is: "+toTest);
			
			Long eventTimeLong = Long.valueOf(eventTime);
			
			if (eventTimeLong < toTest){
				
				ClientLog.d(LOG_TAG, "event "+eventTitle+" is close enough. now checking for location");
				
				if (!calendarCursor.isNull(calendarCursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION))){
					
					String locationString = calendarCursor.getString(calendarCursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION));
					ClientLog.d(LOG_TAG, "location string is: "+locationString);
					
					if (!TextUtils.isEmpty(locationString)) {
						toTest = eventTimeLong;
						foundEvent = true;
						selectedEventTitle = eventTitle;
						selectedLocationString = locationString;
					}
					else {
						ClientLog.w(LOG_TAG, "event "+eventTitle+" has no valid location. should be ignored");
					}
					
				}
				else {
					
					ClientLog.e(LOG_TAG, "location column is null form cursor for "+eventTitle);
					
				}
				
			}
//			calendarCursor.moveToNext();
		}
		
		if (foundEvent){
			ClientLog.d(LOG_TAG, "found "+selectedEventTitle+" event, location string is: "+selectedLocationString);
			AutoEvent result = new AutoEvent();
			result.setTitle(selectedEventTitle);
			result.setStartTime(toTest);
			
			if (!TextUtils.isEmpty(selectedEventTitle)){
				
				Geocoder coder = new Geocoder(mContext);
				try {
					List<Address> addresses = coder.getFromLocationName(selectedEventTitle, 1);
					
					if (addresses != null && addresses.size() > 0){
						
						Double lat = addresses.get(0).getLatitude();
						Double lon = addresses.get(0).getLongitude();
						result.setLat(lat.toString());
						result.setLon(lon.toString());
					}
					else {
						ClientLog.e(LOG_TAG, "Error, cant find address from: "+selectedEventTitle+" trying parsing latlon");
						String[] coordinates = selectedEventTitle.split(", ");
						Double lat = Double.valueOf(coordinates[0]);
						Double lon = Double.valueOf(coordinates[1]);
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
