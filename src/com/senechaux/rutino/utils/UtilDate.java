package com.senechaux.rutino.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class UtilDate {
	public static Date addDays(Date date, int days) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.add(Calendar.DAY_OF_MONTH, days);
		return gc.getTime();
	}

}
