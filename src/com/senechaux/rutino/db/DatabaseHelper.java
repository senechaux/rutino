package com.senechaux.rutino.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "data";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_WALLETS = "wallets";
	public static final String WALLETS_ID = "_id";
	public static final String WALLETS_TITLE = "title";
	public static final String WALLETS_BODY = "body";
	private static final String TAG = "Rutino";

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE = "create table "+ TABLE_WALLETS +" (" 
			+ WALLETS_ID + " integer primary key autoincrement, "
			+ WALLETS_TITLE +" text not null, " 
			+ WALLETS_BODY + " text not null);";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLETS);
		onCreate(db);
	}
}

