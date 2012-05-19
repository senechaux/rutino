package com.senechaux.rutino.db;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.senechaux.rutino.R;
import com.senechaux.rutino.db.entities.Account;
import com.senechaux.rutino.db.entities.AccountType;
import com.senechaux.rutino.db.entities.Currency;
import com.senechaux.rutino.db.entities.PeriodicTransaction;
import com.senechaux.rutino.db.entities.Report;
import com.senechaux.rutino.db.entities.Transaction;
import com.senechaux.rutino.db.entities.Wallet;
import com.senechaux.rutino.utils.FileUtils;
import com.senechaux.rutino.utils.UtilAlarm;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	private static volatile DatabaseHelper databaseHelper = null;

	private static final String TAG = "DataBaseHelper"; // Tag just for the
														// LogCat window
	private static final String DB_PATH = "/data/data/com.senechaux.rutino/databases/rutino.db";
	private static final String DB_NAME = "rutino.db";
	private static final int DATABASE_VERSION = 12;

	private Dao<Wallet, Integer> walletDao;
	private Dao<Account, Integer> accountDao;
	private Dao<AccountType, Integer> accountTypeDao;
	private Dao<Transaction, Integer> transactionDao;
	private Dao<PeriodicTransaction, Integer> periodicTransactionDao;
	private Dao<Currency, Integer> currencyDao;
	private Dao<Report, Integer> reportDao;

	private DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	public static synchronized DatabaseHelper getHelper(final Context mContext) {
		if (databaseHelper == null) {
			databaseHelper = new DatabaseHelper(mContext);
		}
		return databaseHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Wallet.class);
			TableUtils.createTable(connectionSource, Account.class);
			TableUtils.createTable(connectionSource, AccountType.class);
			TableUtils.createTable(connectionSource, Transaction.class);
			TableUtils.createTable(connectionSource, PeriodicTransaction.class);
			TableUtils.createTable(connectionSource, Currency.class);
			TableUtils.createTable(connectionSource, Report.class);
			fillTables();
		} catch (SQLException e) {
			Log.e(TAG, "Unable to create the tables", e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase,
			ConnectionSource connectionSource, int oldVer, int newVer) {
		try {
			TableUtils.dropTable(connectionSource, Wallet.class, true);
			TableUtils.dropTable(connectionSource, Account.class, true);
			TableUtils.dropTable(connectionSource, AccountType.class, true);
			TableUtils.dropTable(connectionSource, Transaction.class, true);
			TableUtils.dropTable(connectionSource, PeriodicTransaction.class,
					true);
			TableUtils.dropTable(connectionSource, Currency.class, true);
			TableUtils.dropTable(connectionSource, Report.class, true);
			onCreate(sqliteDatabase, connectionSource);
		} catch (SQLException e) {
			Log.e(TAG, "Unable to upgrade database from version " + oldVer
					+ " to new " + newVer, e);
		}
	}

	/**
	 * Copies the database file to SDCard
	 * */
	public boolean exportDatabase() throws IOException {
		File dbFile = new File(DB_PATH);
		File exportDir = new File(Environment.getExternalStorageDirectory(), "");
		if (!exportDir.exists()) {
			exportDir.mkdirs();
		}
		File file = new File(exportDir, DB_NAME);
		file.createNewFile();
		FileUtils.copyFile(dbFile, file);

		return true;
	}

	/**
	 * Copies the database file from SDCard
	 * */
	public boolean importDatabase() throws IOException {
		// Close the SQLiteOpenHelper so it will commit the created empty
		// database to internal storage.
		close();
		File newDb = new File(Environment.getExternalStorageDirectory(),
				DB_NAME);
		File oldDb = new File(DB_PATH);
		if (newDb.exists()) {
			FileUtils.copyFile(newDb, oldDb);
			databaseHelper = null;
			return true;
		}
		return false;
	}

	public Dao<Wallet, Integer> getWalletDao() throws SQLException {
		if (walletDao == null) {
			walletDao = getDao(Wallet.class);
		}
		return walletDao;
	}

	public Dao<Account, Integer> getAccountDao() throws SQLException {
		if (accountDao == null) {
			accountDao = getDao(Account.class);
		}
		return accountDao;
	}

	public Dao<AccountType, Integer> getAccountTypeDao() throws SQLException {
		if (accountTypeDao == null) {
			accountTypeDao = getDao(AccountType.class);
		}
		return accountTypeDao;
	}

	public Dao<Transaction, Integer> getTransactionDao() throws SQLException {
		if (transactionDao == null) {
			transactionDao = getDao(Transaction.class);
		}
		return transactionDao;
	}

	public Dao<PeriodicTransaction, Integer> getPeriodicTransactionDao()
			throws SQLException {
		if (periodicTransactionDao == null) {
			periodicTransactionDao = getDao(PeriodicTransaction.class);
		}
		return periodicTransactionDao;
	}

	public Dao<Currency, Integer> getCurrencyDao() throws SQLException {
		if (currencyDao == null) {
			currencyDao = getDao(Currency.class);
		}
		return currencyDao;
	}

	public Dao<Report, Integer> getReportDao() throws SQLException {
		if (reportDao == null) {
			reportDao = getDao(Report.class);
		}
		return reportDao;
	}

	public void deletePeriodicTransaction(Context ctxt,
			PeriodicTransaction perTrans) throws SQLException {
		getPeriodicTransactionDao().deleteById(perTrans.get_id());
		UtilAlarm.cancelAlarm(ctxt, perTrans);
	}

	public void deleteAccount(Context ctxt, Account account)
			throws SQLException {
		// Borramos las transacciones asociadas a la cuenta
		Dao<Transaction, Integer> dao = getTransactionDao();
		DeleteBuilder<Transaction, Integer> db = dao.deleteBuilder();
		db.where().eq(Transaction.ACCOUNT_ID, account.get_id());
		dao.delete(db.prepare());

		// Borramos las transacciones periódicas asociadas a la cuenta
		Dao<PeriodicTransaction, Integer> daoPer = getPeriodicTransactionDao();
		QueryBuilder<PeriodicTransaction, Integer> qbPer = daoPer
				.queryBuilder();
		qbPer.where().eq(PeriodicTransaction.ACCOUNT_ID, account.get_id());
		List<PeriodicTransaction> list = daoPer.query(qbPer.prepare());
		for (PeriodicTransaction periodicTransaction : list) {
			this.deletePeriodicTransaction(ctxt, periodicTransaction);
		}

		// Borramos la cuenta
		getAccountDao().deleteById(account.get_id());
	}

	public void deleteWallet(Context ctxt, Wallet wallet) throws SQLException {
		// Borramos las cuentas asociadas a la cartera
		Dao<Account, Integer> dao = getAccountDao();
		QueryBuilder<Account, Integer> qb = dao.queryBuilder();
		qb.where().eq(Account.WALLET_ID, wallet.get_id());
		List<Account> list = dao.query(qb.prepare());
		for (Account account : list) {
			this.deleteAccount(ctxt, account);
		}

		// Borramos la cuenta
		getWalletDao().deleteById(wallet.get_id());
	}

	private void fillTables() {
		try {
			getAccountTypeDao();
			accountTypeDao.create(new AccountType("Cuenta",
					"Descripción Cuenta corriente"));
			accountTypeDao.create(new AccountType("Tarjeta Crédito",
					"Descripción Tarjeta Crédito"));
			getCurrencyDao();
			currencyDao.create(new Currency("EUR", "Euro", 1.0));
			currencyDao.create(new Currency("ESP", "Peseta", 166.386));
			currencyDao.create(new Currency("USD", "United States Dollar",
					1.3165));
		} catch (SQLException e) {
			Log.e(TAG, "Unable to generate Account Types", e);
		}
	}

}
