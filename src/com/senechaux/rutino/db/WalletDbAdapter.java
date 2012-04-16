package com.senechaux.rutino.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Simple wallets database access helper class. Defines the basic CRUD
 * operations for the walletpad example, and gives the ability to list all
 * wallets as well as retrieve or modify a specific wallet.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class WalletDbAdapter {

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private final Context mCtx;

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx the Context within which to work
	 */
	public WalletDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the wallets database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public WalletDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	/**
	 * Create a new wallet using the title and body provided. If the wallet is
	 * successfully created return the new id for that wallet, otherwise return
	 * a -1 to indicate failure.
	 * 
	 * @param title the title of the wallet
	 * @param body the body of the wallet
	 * @return id or -1 if failed
	 */
	public long createWallet(String title, String body) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DatabaseHelper.WALLETS_TITLE, title);
		initialValues.put(DatabaseHelper.WALLETS_BODY, body);

		return mDb.insert(DatabaseHelper.TABLE_WALLETS, null, initialValues);
	}

	/**
	 * Return a Cursor over the list of all wallets in the database
	 * @return Cursor over all wallets
	 */
	public Cursor readAllWallets() {
		return mDb.query(DatabaseHelper.TABLE_WALLETS, new String[] { DatabaseHelper.WALLETS_ID, DatabaseHelper.WALLETS_TITLE,
				DatabaseHelper.WALLETS_BODY }, null, null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the wallet that matches the given id
	 * @param id of wallet to retrieve
	 * @return Cursor positioned to matching wallet, if found
	 * @throws SQLException if wallet could not be found/retrieved
	 */
	public Cursor readWallet(long id) throws SQLException {
		Cursor mCursor =
				mDb.query(true, DatabaseHelper.TABLE_WALLETS, new String[] { DatabaseHelper.WALLETS_ID, DatabaseHelper.WALLETS_TITLE,
				DatabaseHelper.WALLETS_BODY }, DatabaseHelper.WALLETS_ID + "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Update the wallet using the details provided. The wallet to be updated is
	 * specified using the id, and it is altered to use the title and body
	 * values passed in
	 * 
	 * @param id of wallet to update
	 * @param title value to set wallet title to
	 * @param body value to set wallet body to
	 * @return true if the wallet was successfully updated, false otherwise
	 */
	public boolean updateWallet(long id, String title, String body) {
		ContentValues args = new ContentValues();
		args.put(DatabaseHelper.WALLETS_TITLE, title);
		args.put(DatabaseHelper.WALLETS_BODY, body);

		return mDb.update(DatabaseHelper.TABLE_WALLETS, args, DatabaseHelper.WALLETS_ID + "=" + id, null) > 0;
	}

	/**
	 * Delete the wallet with the given id
	 * @param id of wallet to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteWallet(long id) {
		return mDb.delete(DatabaseHelper.TABLE_WALLETS, DatabaseHelper.WALLETS_ID + "=" + id, null) > 0;
	}

}
