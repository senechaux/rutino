package com.senechaux.rutino.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.widget.Button;

public class StringUtils {
	
	public static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	public static void updateTimeDate(Button date, Button time, GregorianCalendar gc) {
		if (date != null) {
			int mYear = gc.get(Calendar.YEAR);
			int mMonth = gc.get(Calendar.MONTH);
			int mDay = gc.get(Calendar.DAY_OF_MONTH);
			date.setText("" + mDay + "-" + (mMonth + 1) + "-" + mYear);
		}

		if (time != null) {
			int mHour = gc.get(Calendar.HOUR_OF_DAY);
			int mMinute = gc.get(Calendar.MINUTE);
			time.setText("" + StringUtils.pad(mHour) + ":" + StringUtils.pad(mMinute));
		}
	}

}
