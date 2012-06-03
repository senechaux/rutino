package com.senechaux.rutino.db.entities;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.utils.DateUtils;

@DatabaseTable
public class PeriodicTransaction extends Transaction {
	private static final long serialVersionUID = 1L;

	public static final String OBJ = "periodicTransaction";

	@DatabaseField
	private int periodicity;

	public PeriodicTransaction() {
		super();
	}

	public PeriodicTransaction(String name, String desc, Double amount, Date date, Double latitude, Double longitude,
			AccountEntity accountEntity, Currency currency, int periodicity) {
		super();
		this.name = name;
		this.desc = desc;
		this.amount = amount;
		this.date = date;
		this.latitude = latitude;
		this.longitude = longitude;
		this.accountEntity = accountEntity;
		this.currency = currency;
		this.periodicity = periodicity;
	}

	public PeriodicTransaction(Transaction transaction, int periodicity) {
		this.name = transaction.getName();
		this.desc = transaction.getDesc();
		this.amount = transaction.getAmount();
		this.date = transaction.getDate();
		this.latitude = transaction.getLatitude();
		this.longitude = transaction.getLongitude();
		this.accountEntity = transaction.getAccount();
		this.currency = transaction.getCurrency();
		this.periodicity = periodicity;
	}

	public int getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(int periodicity) {
		this.periodicity = periodicity;
	}

	public void setNextDate() {
		this.setDate(DateUtils.addDays(this.getDate(), this.getPeriodicity()));
	}

	@Override
	public String toString() {
		return "PeriodicTransaction [periodicity=" + periodicity + ", name=" + name + ", desc=" + desc + ", amount="
				+ amount + ", date=" + date + ", account=" + accountEntity + ", currency=" + currency + ", _id=" + _id
				+ "]";
	}

	/**
	 * Creates and returns an instance of the account from the provided JSON data.
	 * 
	 * @param account The JSONObject containing account data
	 * @return account The new instance of Voiper user created from the JSON data.
	 */
	public static PeriodicTransaction valueOf(Context ctxt, JSONObject periodicTransaction) {
		try {
			String globalId = periodicTransaction.has("global_id") ? periodicTransaction.getString("global_id") : null;
			String periodicity = periodicTransaction.has("periodicity") ? periodicTransaction.getString("periodicity")
					: null;

			PeriodicTransaction periodicTransactionEntity = new PeriodicTransaction(Transaction.valueOf(ctxt, periodicTransaction), Integer.parseInt(periodicity));
			periodicTransactionEntity.setGlobal_id(globalId);
			return periodicTransactionEntity;
		} catch (Exception ex) {
			Log.i("PeriodicTransaction", "Error parsing JSON PeriodicTransaction object" + ex.toString());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<PeriodicTransaction> dameListadoTransaccionesPeriodicas(
			Context ctxt, AccountEntity accountFather) throws SQLException {
		Dao<PeriodicTransaction, Integer> dao = (Dao<PeriodicTransaction, Integer>) DatabaseHelper.getHelper(ctxt)
				.getMyDao(PeriodicTransaction.class);
		QueryBuilder<PeriodicTransaction, Integer> qb = dao.queryBuilder();
		qb.where().eq(PeriodicTransaction.ACCOUNTENTITY_ID, accountFather.get_id());
		// false: más reciente primero
		qb.orderBy(PeriodicTransaction.DATE, false);
		return dao.query(qb.prepare());
	}

}
