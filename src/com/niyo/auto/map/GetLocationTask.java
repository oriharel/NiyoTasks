package com.niyo.auto.map;

import java.io.InputStream;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Address;
import android.os.AsyncTask;

public class GetLocationTask extends AsyncTask<Void, Void, Void> {
	JSONObject jsonObject;
	String address;
	Address[] addrs;

	public GetLocationTask(String a, Address[] addrs) {
		this.address = a;
		this.addrs = addrs;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		getLocationInfo(address);
		return null;
	}

	private void getLocationInfo(String address) {

		String query = "http://maps.google.com/maps/api/geocode/json?address=" + address.replaceAll(" ","%20")
				+ "&sensor=false";
		Address addr = null;
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(query);

		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {

				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;
				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}
				try {
					jsonObject = new JSONObject(stringBuilder.toString());
					addr = new Address(Locale.getDefault());
					JSONArray addrComp = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
							.getJSONArray("address_components");
					String locality = ((JSONArray)((JSONObject)addrComp.get(0)).get("types")).getString(0);
					if (locality.compareTo("locality") == 0) {
						locality = ((JSONObject)addrComp.get(0)).getString("long_name");
						addr.setLocality(locality);
					}
					String adminArea = ((JSONArray)((JSONObject)addrComp.get(2)).get("types")).getString(0);
					if (adminArea.compareTo("administrative_area_level_1") == 0) {
						adminArea = ((JSONObject)addrComp.get(2)).getString("long_name");
						addr.setAdminArea(adminArea);
					}
					String country = ((JSONArray)((JSONObject)addrComp.get(3)).get("types")).getString(0);
					if (country.compareTo("country") == 0) {
						country = ((JSONObject)addrComp.get(3)).getString("long_name");
						addr.setCountryName(country);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Double lon = Double.valueOf(0);
				Double lat = Double.valueOf(0);

				try {

					lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
						.getJSONObject("geometry").getJSONObject("location")
						.getDouble("lng");

					lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
						.getJSONObject("geometry").getJSONObject("location")
						.getDouble("lat");

				} catch (JSONException e) {
					e.printStackTrace();
				}
				addr.setLatitude(lat);
				addr.setLongitude(lon);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		addrs[0] = addr;

	}
}
