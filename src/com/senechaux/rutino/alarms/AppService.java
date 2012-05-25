package com.senechaux.rutino.alarms;

import java.sql.SQLException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.senechaux.rutino.R;
import com.senechaux.rutino.TransactionEdit;
import com.senechaux.rutino.components.WakefulIntentService;
import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.PeriodicTransaction;
import com.senechaux.rutino.db.entities.Transaction;
import com.senechaux.rutino.utils.AlarmUtils;

public class AppService extends WakefulIntentService {
	private static final String TAG = "AppService"; 
	public AppService() {
		super("AppService");
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		PeriodicTransaction perTrans = (PeriodicTransaction) intent.getSerializableExtra(PeriodicTransaction.OBJ);
		Transaction trans = new Transaction(perTrans);
		try {
			// Insertamos la transacción en BBDD
			DatabaseHelper.getHelper(this).genericCreateOrUpdate(AppService.this, trans);

			// Actualizamos la transacción periódica en BBDD
			perTrans.setNextDate();
			DatabaseHelper.getHelper(this).genericCreateOrUpdate(AppService.this, perTrans);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Establecemos la nueva alarma
		AlarmUtils.setAlarm(this, perTrans);

		// Creamos la notificación
		Intent notificationIntent = new Intent(this, TransactionEdit.class);
		notificationIntent.putExtra(Transaction.OBJ, trans);

		CharSequence from = trans.getName();
		CharSequence message = getResources().getString(R.string.update_transaction);
		PendingIntent pi = PendingIntent.getActivity(this, trans.get_id(), notificationIntent, 0);

		Notification notif = new Notification(R.drawable.rutino_icon, from, System.currentTimeMillis());
		notif.setLatestEventInfo(this, from, message, pi);
		notif.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(trans.get_id(), notif);
	}

}
