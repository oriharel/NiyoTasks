package com.niyo.tasks.map;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.niyo.ClientLog;
import com.niyo.ServiceCaller;

public class AddressGeocodingTask extends AsyncTask<Double, Void, List<Address>> {

	private static final String LOG_TAG = AddressGeocodingTask.class.getSimpleName();
	private Context mContext;
	private ServiceCaller mCaller;
	
	public AddressGeocodingTask(Context context, ServiceCaller caller){
		mContext = context;
		mCaller = caller;
	}
	@Override
	protected List<Address> doInBackground(Double... params) {
		
		Geocoder coder = new Geocoder(mContext);
		
		Double lat = params[0];
		Double lon = params[1];
		
		try {
			List<Address> addresses = coder.getFromLocation(lat, lon, 4);
			
			return addresses;
//			if (addresses != null && addresses.size() > 0){
//				
//				EditText addressSearch = (EditText)findViewById(R.id.boxSearchEdit);
//				addressSearch.setText(addresses.get(0).getAddressLine(0));
//			}
		} catch (IOException e) {
			ClientLog.e(LOG_TAG, "Error!", e);
		}
		
		return null;
	}
	
	@Override
    protected void onPostExecute(List<Address> result) 
	{
        if (isCancelled()) 
        {
        	ClientLog.d(LOG_TAG, "isCancelled activated");
        }
        
        ClientLog.d(LOG_TAG, "calling success");
        try 
        {
        	if (mCaller != null){
        		mCaller.success(result);
        	}
			
		} 
        catch (Exception e) 
        {
			ClientLog.e(LOG_TAG, "Error! with ", e);
		}
    }

}
