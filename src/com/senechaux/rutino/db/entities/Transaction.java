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

@DatabaseTable
public class Transaction extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public static final String OBJ = "transaction";
	public static final String NAME = "name";
	public static final String DESC = "desc";
	public static final String AMOUNT = "amount";
	public static final String DATE = "date";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String ACCOUNTENTITY_ID = "accountEntity_id";
	public static final String CURRENCY_ID = "currency_id";

	@DatabaseField
	protected String name;
	@DatabaseField
	protected String desc;
	@DatabaseField
	protected Double amount;
	@DatabaseField
	protected Date date;
	@DatabaseField
	protected Double latitude;
	@DatabaseField
	protected Double longitude;
	@DatabaseField(canBeNull = false, foreign = true)
	protected AccountEntity accountEntity;
	@DatabaseField(canBeNull = false, foreign = true)
	protected Currency currency;

	public Transaction() {
		super();
	}

	public Transaction(String name, String desc, Double amount, Date date, Double latitude, Double longitude,
			AccountEntity accountEntity, Currency currency) {
		super();
		this.name = name;
		this.desc = desc;
		this.amount = amount;
		this.date = date;
		this.latitude = latitude;
		this.longitude = longitude;
		this.accountEntity = accountEntity;
		this.currency = currency;
	}

	public Transaction(Transaction perTrans) {
		super();
		this.setName(perTrans.getName());
		this.setDesc(perTrans.getDesc());
		this.setAmount(perTrans.getAmount());
		this.setDate(perTrans.getDate());
		this.setLatitude(perTrans.getLatitude());
		this.setLongitude(perTrans.getLongitude());
		this.setAccount(perTrans.getAccount());
		this.setCurrency(perTrans.getCurrency());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public AccountEntity getAccount() {
		return accountEntity;
	}

	public void setAccount(AccountEntity accountEntity) {
		this.accountEntity = accountEntity;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return "Transaction [id=" + this.get_id() + ", name=" + name + ", desc=" + desc + ", amount=" + amount
				+ ", account=" + accountEntity + "]";
	}

	/**
	 * Creates and returns an instance of the account from the provided JSON data.
	 * 
	 * @param account
	 *            The JSONObject containing account data
	 * @return account The new instance of Voiper user created from the JSON data.
	 */
	@SuppressWarnings("unchecked")
	public static Transaction valueOf(Context ctxt, JSONObject transaction) {
		try {
			String globalId = transaction.has("global_id") ? transaction.getString("global_id") : null;
			String name = transaction.has("name") ? transaction.getString("name") : null;
			String description = transaction.has("description") ? transaction.getString("description")
					: null;
			String amount = transaction.has("amount") ? transaction.getString("amount") : null;
			String date = transaction.has("date") ? transaction.getString("date") : null;
			String latitude = transaction.has("latitude") ? transaction.getString("latitude") : null;
			String longitude = transaction.has("longitude") ? transaction.getString("longitude") : null;
			String accountEntityGlobalId = transaction.has("account_global_id") ? transaction
					.getString("account_global_id") : null;
			String currencyGlobalId = transaction.has("currency_global_id") ? transaction
					.getString("currency_global_id") : null;

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
			Transaction periodicTransactionEntity = new Transaction(name, description,
					Double.parseDouble(amount), df.parse(date), Double.parseDouble(latitude),
					Double.parseDouble(longitude), accountEntity, currency);
			periodicTransactionEntity.setGlobal_id(globalId);
			return periodicTransactionEntity;
		} catch (Exception ex) {
			Log.i("Wallet", "Error parsing JSON AccountType object" + ex.toString());
		}
		return null;
	}

}
