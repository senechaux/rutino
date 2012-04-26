package com.senechaux.rutino.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.senechaux.rutino.R;
import com.senechaux.rutino.db.entities.Account;
import com.senechaux.rutino.db.entities.AccountType;
import com.senechaux.rutino.db.entities.Currency;
import com.senechaux.rutino.db.entities.Transaction;
import com.senechaux.rutino.db.entities.Wallet;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "rutino.db";
	private static final int DATABASE_VERSION = 7;

	private Dao<Wallet, Integer> walletDao;
	private Dao<Account, Integer> accountDao;
	private Dao<AccountType, Integer> accountTypeDao;
	private Dao<Transaction, Integer> transactionDao;
	private Dao<Currency, Integer> currencyDao;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Wallet.class);
			TableUtils.createTable(connectionSource, Account.class);
			TableUtils.createTable(connectionSource, AccountType.class);
			TableUtils.createTable(connectionSource, Transaction.class);
			TableUtils.createTable(connectionSource, Currency.class);
			fillTables();
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to create datbases", e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
		try {
			TableUtils.dropTable(connectionSource, Wallet.class, true);
			TableUtils.dropTable(connectionSource, Account.class, true);
			TableUtils.dropTable(connectionSource, AccountType.class, true);
			TableUtils.dropTable(connectionSource, Transaction.class, true);
			TableUtils.dropTable(connectionSource, Currency.class, true);
			onCreate(sqliteDatabase, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVer + " to new "
					+ newVer, e);
		}
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

	public Dao<Currency, Integer> getCurrencyDao() throws SQLException {
		if (currencyDao == null) {
			currencyDao = getDao(Currency.class);
		}
		return currencyDao;
	}

	private void fillTables() {
		try {
			getAccountTypeDao();
			accountTypeDao.create(new AccountType("Cuenta", "Descripción Cuenta corriente"));
			accountTypeDao.create(new AccountType("Tarjeta Crédito", "Descripción Tarjeta Crédito"));
			getCurrencyDao();
			currencyDao.create(new Currency("EUR", "Euro", 1.0));
			currencyDao.create(new Currency("ESP", "Peseta", 166.386));
			currencyDao.create(new Currency("USD", "United States Dollar", 1.3165));
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to generate Account Types", e);
		}
	}

}
