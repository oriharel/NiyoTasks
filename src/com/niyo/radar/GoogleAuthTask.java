package com.niyo.radar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.niyo.ClientLog;
import com.niyo.ServiceCaller;
import com.niyo.SettingsManager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

public class GoogleAuthTask extends AsyncTask<Void, Void, Void> {
	
	private static final String LOG_TAG = GoogleAuthTask.class.getSimpleName();
	private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
	protected ServiceCaller mCaller;
	protected String mEmail;
	protected Context mContext;
	protected DialogInterface mDialog;
	private static final String IMAGE_KEY = "picture";
	private static final String USER_ID_KEY = "id";
	public static final String PROFILE_IMAGE_URL = "profile_url";
	public static final String PROFILE_ID = "user_id";
	
	public GoogleAuthTask(Context context, ServiceCaller caller, DialogInterface dialog, String email) {
		this.mCaller = caller;
		this.mEmail = email;
		this.mDialog = dialog;
		this.mContext = context;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		
		try {
	        fetchNameFromProfileServer();
	      } catch (IOException ex) {
	    	  ClientLog.e(LOG_TAG, "Following Error occured, please try again. " + ex.getMessage(), ex);
	      } catch (JSONException e) {
	    	  ClientLog.e(LOG_TAG, "Bad response: " + e.getMessage(), e);
	      }
	      return null;
	}
	
	private void fetchNameFromProfileServer() throws IOException, JSONException {
        String token = fetchToken();
        if (token == null) {
          // error has already been handled in fetchToken()
          return;
        }
        URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + token);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        int sc = con.getResponseCode();
        if (sc == 200) {
          InputStream is = con.getInputStream();
          String jsonResponse = readResponse(is);
          String profileImageUrl = getProfileImage(jsonResponse);
          String userId = getUserId(jsonResponse);
          SettingsManager.setString(mContext, PROFILE_IMAGE_URL, profileImageUrl);
          SettingsManager.setString(mContext, PROFILE_ID, userId);
          is.close();
          return;
        } else if (sc == 401) {
            GoogleAuthUtil.invalidateToken(mContext, token);
            ClientLog.e(LOG_TAG, "Server auth error, please try again.");
            Log.i(LOG_TAG, "Server auth error: " + readResponse(con.getErrorStream()));
            return;
        } else {
        	ClientLog.e(LOG_TAG, "Server returned the following error code: " + sc);
          return;
        }
    }
	
	private String getProfileImage(String jsonResponse) throws JSONException {
	      Log.d("map test", jsonResponse);
	      JSONObject profile = new JSONObject(jsonResponse);
	      return profile.getString(IMAGE_KEY);
	    }
	
	private String getUserId(String jsonResponse) throws JSONException {
		JSONObject profile = new JSONObject(jsonResponse);
		return profile.getString(USER_ID_KEY);
	}
	
	private static String readResponse(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len = 0;
        while ((len = is.read(data, 0, data.length)) >= 0) {
            bos.write(data, 0, len);
        }
        return new String(bos.toByteArray(), "UTF-8");
    }
	
	
	protected String fetchToken() throws IOException {
		try {
			return GoogleAuthUtil.getToken(mContext, mEmail, SCOPE);
		} catch (GooglePlayServicesAvailabilityException playEx) {
			// GooglePlayServices.apk is either old, disabled, or not present.
			ClientLog.e(LOG_TAG, "Error with google play", playEx);
		} catch (UserRecoverableAuthException userRecoverableException) {
			// Unable to authenticate, but the user can fix this.
			// Forward the user to the appropriate activity.
			((Activity)mContext).startActivityForResult(userRecoverableException.getIntent(), 0);
		} catch (GoogleAuthException fatalException) {
			ClientLog.e(LOG_TAG, "Unrecoverable error " + fatalException.getMessage(), fatalException);
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		
		ClientLog.d(LOG_TAG, "onPost called");
		mDialog.dismiss();
		((Activity)mContext).invalidateOptionsMenu();
		mCaller.success(result);
    }

}
