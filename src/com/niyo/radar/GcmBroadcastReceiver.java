package com.niyo.radar;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.niyo.ClientLog;
import com.niyo.R;
import com.niyo.auto.AutoActivity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class GcmBroadcastReceiver extends BroadcastReceiver {

	static final String LOG_TAG = GcmBroadcastReceiver.class.getSimpleName();
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;
    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        ctx = context;
        String message = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(message)) {
            sendNotification("Send error: " + intent.getExtras().toString());
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(message)) {
            sendNotification("Deleted messages on server: " +
                    intent.getExtras().toString());
        } else {
            sendNotification("Received: " + intent.getExtras().toString());
        }
        
        ClientLog.d(LOG_TAG, "got push with msg "+intent.getExtras().toString());
        
			
		String gcmType = intent.getStringExtra("Type");
		
		if (gcmType.equals("req")) {
			//fire an update location intent
			
			String userAsking = intent.getStringExtra("user_asking");
			String trxId = intent.getStringExtra("trx_id");
			
			ClientLog.d(LOG_TAG, "got location request from "+userAsking);
			
			Intent serviceIntent = new Intent(context, LocationUpdaterIntentService.class);
			serviceIntent.putExtra(LocationUpdaterIntentService.USER_ASKING_PROPERTY, userAsking);
			serviceIntent.putExtra(LocationUpdaterIntentService.TRX_ID_PROPERTY, trxId);
			context.startService(serviceIntent);
		}
		
		else if (gcmType.equals("ack")) {
			ClientLog.d(LOG_TAG, "registration performed successfully");
		}
		
		else if (gcmType.equals("res")) {
			
			String userAnswering = intent.getStringExtra("user_answering");
			String lat = intent.getStringExtra("latitude");
			String lon = intent.getStringExtra("longitude");
			String updateTimeStr = intent.getStringExtra("updateTime");
			String friendImageUrl = intent.getStringExtra("imageUrl");
			String trxId = intent.getStringExtra("trx_id");
			
			Long updateTimeInMillis;
			Intent updateIntent = new Intent("com.niyo.updateFriend");
			
			try {
				updateTimeInMillis = Long.valueOf(updateTimeStr);
				Calendar now = Calendar.getInstance();
				long diff = now.getTimeInMillis() - updateTimeInMillis;
				
				ClientLog.d(LOG_TAG, "diff time is "+diff);
				
				long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);
				
				String updateTime = "Updated: "+diffInMinutes+" min. ago";
				updateIntent.putExtra(RadarBroadcastReceiver.FRIEND_UPDATE_TIME, updateTime);
			}
			catch(Exception e) {
				ClientLog.e(LOG_TAG, "Error in parsgin update time "+updateTimeStr);
			}
			
			
			
			ClientLog.d(LOG_TAG, "your friend, "+userAnswering+" is in lat: "+lat+" and lon: "+lon+" trxId is: "+trxId);
			
			
			updateIntent.putExtra(RadarBroadcastReceiver.FRIEND_EMAIL, userAnswering);
			updateIntent.putExtra(RadarBroadcastReceiver.FRIEND_LAT, Double.valueOf(lat));
			updateIntent.putExtra(RadarBroadcastReceiver.FRIEND_LON, Double.valueOf(lon));
			updateIntent.putExtra(RadarBroadcastReceiver.FRIENDS_IMAGE_URL, friendImageUrl);
			context.sendBroadcast(updateIntent);
		}
        
    }

    // Put the GCM message into a notification and post it.
    private void sendNotification(String msg) {
//        mNotificationManager = (NotificationManager)
//                ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
//                new Intent(ctx, AutoActivity.class), 0);
//
//        Notification.Builder mBuilder =
//                new Notification.Builder(ctx)
//        .setSmallIcon(R.drawable.ic_launcher)
//        .setContentTitle("GCM Notification")
//        .setStyle(new Notification.BigTextStyle()
//        .bigText(msg))
//        .setContentText(msg);
//
//        mBuilder.setContentIntent(contentIntent);
//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
