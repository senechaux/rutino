package com.senechaux.rutino.db.entities;

import java.text.DateFormat;
import java.util.Date;

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
	 * @param account
	 *            The JSONObject containing account data
	 * @return account The new instance of Voiper user created from the JSON data.
	 */
	@SuppressWarnings("unchecked")
	public static PeriodicTransaction valueOf(Context ctxt, JSONObject periodicTransaction) {
		try {
			String globalId = periodicTransaction.has("global_id") ? periodicTransaction.getString("global_id") : null;
			String name = periodicTransaction.has("name") ? periodicTransaction.getString("name") : null;
			String description = periodicTransaction.has("description") ? periodicTransaction.getString("description")
					: null;
			String amount = periodicTransaction.has("amount") ? periodicTransaction.getString("amount") : null;
			String date = periodicTransaction.has("date") ? periodicTransaction.getString("date") : null;
			String latitude = periodicTransaction.has("latitude") ? periodicTransaction.getString("latitude") : null;
			String longitude = periodicTransaction.has("longitude") ? periodicTransaction.getString("longitude") : null;
			String accountEntityGlobalId = periodicTransaction.has("account_global_id") ? periodicTransaction
					.getString("account_global_id") : null;
			String currencyGlobalId = periodicTransaction.has("currency_global_id") ? periodicTransaction
					.getString("currency_global_id") : null;
			String periodicity = periodicTransaction.has("periodicity") ? periodicTransaction.getString("periodicity")
					: null;

			Dao<AccountEntity, Integer> daoAccountEntity = (Dao<AccountEntity, Integer>) DatabaseHelper.getHelper(ctxt)
					.getMyDao(AccountEntity.class);
			QueryBuilder<AccountEntity, Integer> qbAccountEntity = daoAccountEntity.queryBuilder();
			qbAccountEntity.where().eq(AccountEntity.GLOBAL_ID, accountEntityGlobalId);
			AccountEntity accountEntity = daoAccountEntity.queryForFirst(qbAccountEntity.prepare());

			Dao<Currency, Integer> daoCurrency = (Dao<Currency, Integer>) DatabaseHelper.getHelper(ctxt).getMyDao(
					Currency.class);
			QueryBuilder<Currency, Integer> qbCurrency = daoCurrency.queryBuilder();
			qbCurrency.where().eq(Currency.GLOBAL_ID, currencyGlobalId);
			Currency currency = daoCurrency.queryForFirst(qbCurrency.prepare());

			DateFormat df = DateFormat.getDateInstance();
			PeriodicTransaction periodicTransactionEntity = new PeriodicTransaction(name, description,
					Double.parseDouble(amount), df.parse(date), Double.parseDouble(latitude),
					Double.parseDouble(longitude), accountEntity, currency, Integer.parseInt(periodicity));
			periodicTransactionEntity.setGlobal_id(globalId);
			return periodicTransactionEntity;
		} catch (Exception ex) {
			Log.i("Wallet", "Error parsing JSON AccountType object" + ex.toString());
		}
		return null;
	}

}
