package org.msf.android.services;

import java.util.Calendar;

import org.msf.android.R;
import org.msf.android.tasks.ExportCSVDataMalnutrition;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class InternetStatusReceiver extends BroadcastReceiver {

	public static String REMINDER_ACTION = "org.msf.android.REMINDER_ACTION";
	public static String SCHEDULED_BACKUP = "org.msf.android.SCHEDULED_BACKUP";

	public static int REMINDER_NOTIFICATION_ID = 12345;

	@Override
	public void onReceive(Context context, Intent intent) {

		final String action = intent.getAction();

		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			Bundle extras = intent.getExtras();
			if (extras.getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY)) {
				return;
			} else {
				// Note: This gets called TWICE every time you get a connection
				ConnectivityManager conMgr = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
				if (activeNetwork != null && activeNetwork.isConnected()) {
					onConnectionDetected(context);
				}
			}
		}

		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			onBootCompleted(context);
		}

		if (action.equals(REMINDER_ACTION)) {
			onReminder(context);
		}

		if (action.equals(SCHEDULED_BACKUP)) {

		}
	}

	public void onConnectionDetected(Context context) {
		/*
		Toast.makeText(context, "WIFI CONNECTED", Toast.LENGTH_LONG).show();

		ExportCSVDataMalnutrition csvExportTask = new ExportCSVDataMalnutrition(
				null);
		csvExportTask.execute();
		*/
	}

	public void onBootCompleted(Context context) {
		Log.d(this.getClass().getCanonicalName(), "onBootCompleted");
		// set daily alarm
		Intent alarmIntent = new Intent(REMINDER_ACTION);
		PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context,
				0, alarmIntent, Intent.FLAG_FROM_BACKGROUND);

		// 1800?
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 18);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		int alarmType = AlarmManager.RTC_WAKEUP;
		long timeForFirstExecution = calendar.getTimeInMillis();
		long intervalForExecution = AlarmManager.INTERVAL_DAY;

		Intent backupIntent = new Intent(SCHEDULED_BACKUP);
		PendingIntent pendingBackupIntent = PendingIntent.getBroadcast(context,
				0, backupIntent, Intent.FLAG_FROM_BACKGROUND);

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(alarmType, timeForFirstExecution, intervalForExecution,
				pendingAlarmIntent);
		am.setRepeating(alarmType, timeForFirstExecution, intervalForExecution,
				pendingBackupIntent);
	}

	public void onReminder(Context context) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int notificationDefaults = Notification.DEFAULT_ALL;
		Notification notification = new NotificationCompat.Builder(context)
				.setContentTitle(
						context.getString(R.string.malnutrition_periodic_reminder_title))
				.setContentText(
						context.getString(R.string.malnutrition_periodic_reminder_message))
				.setSmallIcon(R.drawable.icon_launcher)
				.setDefaults(notificationDefaults).getNotification();
		nm.notify(REMINDER_NOTIFICATION_ID, notification);
	}

	public void onScheduledBackup(Context context) {
		Toast.makeText(context, "Backing up data", Toast.LENGTH_LONG).show();
		
		ExportCSVDataMalnutrition csvExportTask = new ExportCSVDataMalnutrition(
				null);
		csvExportTask.execute();
	}
}
