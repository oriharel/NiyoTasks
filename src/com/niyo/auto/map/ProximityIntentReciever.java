package com.niyo.auto.map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.niyo.R;
import com.niyo.Utils;
import com.niyo.tasks.CategoryTasksActivity;

public class ProximityIntentReciever extends BroadcastReceiver {

	public static final String TASK_ID_PROXIMITY = "taskIdProx";
	public static final String TASK_PROXIMITY_ALERT = "com.niyo.taskalert";

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String key = LocationManager.KEY_PROXIMITY_ENTERING;
		
		Boolean entering = intent.getBooleanExtra(key, false);
		
		if (entering){
			
			String taskId = intent.getStringExtra(TASK_ID_PROXIMITY);
			
			JSONObject tasksJson = Utils.getTasksFromProvider(context);
			
			try {
				
				JSONArray tasks = tasksJson.getJSONArray("tasks");
				for (int i = 0; i < tasks.length(); i++){
					
					String id = tasks.getJSONObject(i).getString("id");
					
					if (id.equals(taskId)){
						
						Intent notifIntent = new Intent(context, CategoryTasksActivity.class);
						
						PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
								notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);
						
//						Builder notification = new Builder(context);
//						notification.setTicker("A task is near!");
//						notification.setContentInfo(tasks.getJSONObject(i).getString("content"));
//						notification.setSmallIcon(R.drawable.ic_launcher);
//						notification.se |= Notification.FLAG_AUTO_CANCEL;
						
						long when = System.currentTimeMillis();
						
						Notification notification = new Notification(R.drawable.ic_launcher, "", when);
						
						notification.setLatestEventInfo(context, "A task is close by!", tasks.getJSONObject(i).getString("content"),
								contentIntent);
						
						notification.ledARGB = 0xff0000FF;
						notification.ledOnMS = 300;
						notification.ledOffMS = 1000;
						notification.flags |= Notification.FLAG_SHOW_LIGHTS;
						notification.flags |= Notification.FLAG_AUTO_CANCEL;
						
						NotificationManager notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
						
						notifManager.notify(0, notification);
					}
				}
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
		}
	}

}
