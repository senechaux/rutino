package com.senechaux.rutino.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.senechaux.rutino.alarms.OnAlarmReceiver;
import com.senechaux.rutino.db.entities.PeriodicTransaction;

public class UtilAlarm {

	public static void setAlarm(Context ctxt, PeriodicTransaction perTrans) {
		// Establece alarma
		Intent intent = new Intent(ctxt, OnAlarmReceiver.class);
		intent.putExtra(PeriodicTransaction.OBJ, perTrans);
		PendingIntent pi = PendingIntent.getBroadcast(ctxt, perTrans.get_id(),
				intent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager am = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, perTrans.getDate().getTime(),
				pi);
	}
	
	public static void cancelAlarm(Context ctxt, PeriodicTransaction perTrans) {
		// Cancela alarma
		Intent intent = new Intent(ctxt, OnAlarmReceiver.class);
		intent.putExtra(PeriodicTransaction.OBJ, perTrans);
		PendingIntent pi = PendingIntent.getBroadcast(ctxt, perTrans.get_id(),
				intent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager am = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}
}
