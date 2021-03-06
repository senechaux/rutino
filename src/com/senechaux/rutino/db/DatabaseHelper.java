package com.senechaux.rutino.db;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.senechaux.rutino.Constants;
import com.senechaux.rutino.R;
import com.senechaux.rutino.db.entities.AccountEntity;
import com.senechaux.rutino.db.entities.AccountType;
import com.senechaux.rutino.db.entities.BaseEntity;
import com.senechaux.rutino.db.entities.Currency;
import com.senechaux.rutino.db.entities.PeriodicTransaction;
import com.senechaux.rutino.db.entities.Report;
import com.senechaux.rutino.db.entities.Transaction;
import com.senechaux.rutino.db.entities.Wallet;
import com.senechaux.rutino.utils.AlarmUtils;
import com.senechaux.rutino.utils.FileUtils;

@SuppressWarnings("unchecked")
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	private static volatile DatabaseHelper databaseHelper = null;

	private static final String TAG = "DataBaseHelper";
	private static final String DB_PATH = "/data/data/com.senechaux.rutino/databases/rutino.db";
	private static final String DB_NAME = "rutino.db";
	private static final int DATABASE_VERSION = 1;

	private Map<String, Dao<?, Integer>> daos = new HashMap<String, Dao<?, Integer>>();

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
	public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Wallet.class);
			TableUtils.createTable(connectionSource, AccountEntity.class);
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
	public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
		try {
			TableUtils.dropTable(connectionSource, Wallet.class, true);
			TableUtils.dropTable(connectionSource, AccountEntity.class, true);
			TableUtils.dropTable(connectionSource, AccountType.class, true);
			TableUtils.dropTable(connectionSource, Transaction.class, true);
			TableUtils.dropTable(connectionSource, PeriodicTransaction.class, true);
			TableUtils.dropTable(connectionSource, Currency.class, true);
			TableUtils.dropTable(connectionSource, Report.class, true);
			onCreate(sqliteDatabase, connectionSource);
		} catch (SQLException e) {
			Log.e(TAG, "Unable to upgrade database from version " + oldVer + " to new " + newVer, e);
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
		File newDb = new File(Environment.getExternalStorageDirectory(), DB_NAME);
		File oldDb = new File(DB_PATH);
		if (newDb.exists()) {
			FileUtils.copyFile(newDb, oldDb);
			databaseHelper = null;
			return true;
		}
		return false;
	}

	public Dao<?, ?> getMyDao(Class<?> entityClass) throws SQLException {
		if (this.daos.get(entityClass.getName()) == null) {
			this.daos.put(entityClass.getName(), (Dao<BaseEntity, Integer>) getDao(entityClass));
		}
		return this.daos.get(entityClass.getName());
	}

	public int genericCreateOrUpdate(Context ctxt, BaseEntity entity) throws SQLException {
		Dao<BaseEntity, Integer> dao = (Dao<BaseEntity, Integer>) DatabaseHelper.getHelper(ctxt).getMyDao(
				entity.getClass());
		if (entity.getGlobal_id() != null) {
			QueryBuilder<BaseEntity, Integer> qb = dao.queryBuilder();
			qb.where().eq(BaseEntity.GLOBAL_ID, entity.getGlobal_id());
			BaseEntity dbEntity = dao.queryForFirst(qb.prepare());
			if (dbEntity != null) {
				entity.set_id(dbEntity.get_id());
			}
		}
		CreateOrUpdateStatus status = dao.createOrUpdate(entity);
		if (entity.getGlobal_id() == null) {
			entity.setGlobal_id(Constants.PREFIX_GLOBAL_ID + entity.get_id());
			return dao.update(entity);
		}
		return status.getNumLinesChanged();
	}

	public void deletePeriodicTransaction(Context ctxt, PeriodicTransaction perTrans) throws SQLException {
		((Dao<PeriodicTransaction, Integer>) getMyDao(PeriodicTransaction.class)).deleteById(perTrans.get_id());
		AlarmUtils.cancelAlarm(ctxt, perTrans);
	}

	public void deleteAccount(Context ctxt, AccountEntity accountEntity) throws SQLException {
		// Borramos las transacciones asociadas a la cuenta
		Dao<Transaction, Integer> dao = (Dao<Transaction, Integer>) getMyDao(Transaction.class);
		DeleteBuilder<Transaction, Integer> db = dao.deleteBuilder();
		db.where().eq(Transaction.ACCOUNTENTITY_ID, accountEntity.get_id());
		dao.delete(db.prepare());

		// Borramos las transacciones periódicas asociadas a la cuenta
		Dao<PeriodicTransaction, Integer> daoPer = (Dao<PeriodicTransaction, Integer>) getMyDao(PeriodicTransaction.class);
		QueryBuilder<PeriodicTransaction, Integer> qbPer = daoPer.queryBuilder();
		qbPer.where().eq(PeriodicTransaction.ACCOUNTENTITY_ID, accountEntity.get_id());
		List<PeriodicTransaction> list = daoPer.query(qbPer.prepare());
		for (PeriodicTransaction periodicTransaction : list) {
			this.deletePeriodicTransaction(ctxt, periodicTransaction);
		}

		// Borramos la cuenta
		((Dao<AccountEntity, Integer>) getMyDao(AccountEntity.class)).deleteById(accountEntity.get_id());
	}

	public void deleteWallet(Context ctxt, Wallet wallet) throws SQLException {
		// Borramos las cuentas asociadas a la cartera
		Dao<AccountEntity, Integer> dao = (Dao<AccountEntity, Integer>) getMyDao(AccountEntity.class);
		QueryBuilder<AccountEntity, Integer> qb = dao.queryBuilder();
		qb.where().eq(AccountEntity.WALLET_ID, wallet.get_id());
		List<AccountEntity> list = dao.query(qb.prepare());
		for (AccountEntity accountEntity : list) {
			this.deleteAccount(ctxt, accountEntity);
		}

		// Borramos la cuenta
		((Dao<Wallet, Integer>) getMyDao(Wallet.class)).deleteById(wallet.get_id());
	}

	private void fillTables() {
		try {
			Dao<AccountType, Integer> accountTypeDao = (Dao<AccountType, Integer>) getMyDao(AccountType.class);
			AccountType at = new AccountType("Cuenta", "Descripción Cuenta corriente");
			at.setGlobal_id("w_1");
			accountTypeDao.create(at);
			at = new AccountType("Tarjeta Crédito", "Descripción Tarjeta Crédito");
			at.setGlobal_id("w_2");
			accountTypeDao.create(at);
			Dao<Currency, Integer> currencyDao = (Dao<Currency, Integer>) getMyDao(Currency.class);
			Currency curr = new Currency("EUR", "Euro", 1.0);
			curr.setGlobal_id("w_1");
			currencyDao.create(curr);
			curr = new Currency("ESP", "Peseta", 166.386);
			curr.setGlobal_id("w_2");
			currencyDao.create(curr);
			curr = new Currency("USD", "United States Dollar", 1.3165);
			curr.setGlobal_id("w_3");
			currencyDao.create(curr);
		} catch (SQLException e) {
			Log.e(TAG, "Unable to generate Account Types", e);
		}
	}

}
