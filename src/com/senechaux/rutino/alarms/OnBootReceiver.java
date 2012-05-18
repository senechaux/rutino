package com.senechaux.rutino.alarms;

import java.sql.SQLException;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.PeriodicTransaction;
import com.senechaux.rutino.utils.UtilAlarm;

public class OnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		List<PeriodicTransaction> list = null;
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		try {
			list = DatabaseHelper.getInstance(context).getPeriodicTransactionDao().queryForAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (PeriodicTransaction perTrans : list) {
			if (perTrans.getDate().getTime() < System.currentTimeMillis()) {
				Intent i = new Intent(context, OnAlarmReceiver.class);
				i.putExtra(PeriodicTransaction.OBJ, perTrans);
				PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
				am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
						pi);
			} else {
				UtilAlarm.setAlarm(context, perTrans);
			}
		}
		
	}
}
