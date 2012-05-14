package com.senechaux.rutino.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.senechaux.rutino.components.WakefulIntentService;

public class OnAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		WakefulIntentService.sendWakefulWork(context, AppService.class);
	}
}