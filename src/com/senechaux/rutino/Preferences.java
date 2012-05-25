package com.senechaux.rutino;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	private static final String TAG = "Preferences"; 

	public static void callMe(Context c) {
		Intent intent = new Intent(c, Preferences.class);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	}
}