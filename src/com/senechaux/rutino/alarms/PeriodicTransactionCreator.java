package com.senechaux.rutino.alarms;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PeriodicTransactionCreator extends BroadcastReceiver {

	NotificationManager nm;

	@Override
	public void onReceive(Context context, Intent intent) {
//		nm = (NotificationManager) context
//				.getSystemService(Context.NOTIFICATION_SERVICE);
//		CharSequence from = context.getResources().getString(R.string.app_name);
//		CharSequence message = context.getResources().getString(R.string.notifMessage);
//
//		Intent notificationIntent = new Intent(context, TaskEdit.class);
//		notificationIntent.putExtras(intent.getExtras());
//		
//		Long mRowId = (Long)intent.getSerializableExtra(TasksDbAdapter.KEY_ROWID);
//
//		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
//				notificationIntent, 0);
//
//		Notification notif = new Notification(R.drawable.icon,
//				context.getResources().getString(R.string.notifTicker), System.currentTimeMillis());
//		notif.flags|=Notification.FLAG_AUTO_CANCEL;
//
//		notif.setLatestEventInfo(context, from, message, contentIntent);
//		nm.notify(mRowId.intValue(), notif);
	}
}
