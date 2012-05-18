package com.senechaux.rutino.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.senechaux.rutino.components.WakefulIntentService;
import com.senechaux.rutino.db.entities.PeriodicTransaction;

public class OnAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		PeriodicTransaction perTrans = (PeriodicTransaction) intent
				.getSerializableExtra(PeriodicTransaction.OBJ);
		Bundle extras = new Bundle();
		extras.putSerializable(PeriodicTransaction.OBJ, perTrans);
		WakefulIntentService.sendWakefulWork(context, AppService.class, extras);
	}
}