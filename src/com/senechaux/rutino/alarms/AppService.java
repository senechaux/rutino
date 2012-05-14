package com.senechaux.rutino.alarms;

import android.content.Intent;

import com.senechaux.rutino.components.WakefulIntentService;

public class AppService extends WakefulIntentService {
	public AppService() {
		super("AppService");
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		// OBTENER LAS TRANSACCIONES PERIODICAS 
		// PARA < CURRENT INSERTAR TRANSACCION HASTA QUE FECHA > CURRENT
		// PARA > CURRENT PROGRAMAR ALARMA CON ALARMMANAGER
	}
}
