package com.senechaux.rutino.db.entities;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.senechaux.rutino.db.DatabaseHelper;

@DatabaseTable
public class AccountEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public static final String OBJ = "accountEntity";
	public static final String NAME = "name";
	public static final String DESC = "desc";
	public static final String WALLET_ID = "wallet_id";
	public static final String ACCOUNTTYPE_ID = "accountType_id";

	@DatabaseField(canBeNull = false)
	private String name;
	@DatabaseField
	private String desc;
	@DatabaseField(canBeNull = false, foreign = true)
	private Wallet wallet;
	@DatabaseField(canBeNull = false, foreign = true)
	private AccountType accountType;

	public AccountEntity() {
		super();
	}

	public AccountEntity(String name, String desc, Wallet wallet, AccountType accountType) {
		super();
		this.name = name;
		this.desc = desc;
		this.wallet = wallet;
		this.accountType = accountType;
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

	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	@Override
	public String toString() {
		return "Account [id=" + this.get_id() + ", name=" + name + ", desc=" + desc + ", wallet=" + wallet + "]";
	}

	// Devuelve el total convertido a la moneda pedida
	@SuppressWarnings("unchecked")
	public Double getTotal(Context ctxt, Currency currency) throws SQLException {
		Dao<Transaction, Integer> dao = (Dao<Transaction, Integer>) DatabaseHelper.getHelper(ctxt).getMyDao(
				Transaction.class);
		QueryBuilder<Transaction, Integer> qb = dao.queryBuilder();
		qb.where().eq(Transaction.ACCOUNTENTITY_ID, this.get_id());
		List<Transaction> list = dao.query(qb.prepare());

		Dao<Currency, Integer> daoCurrency = (Dao<Currency, Integer>) DatabaseHelper.getHelper(ctxt).getMyDao(
				Currency.class);

		Double res = 0.0;
		for (Transaction t : list) {
			if (t.getCurrency().get_id() != 1) {
				res += t.getAmount() / daoCurrency.queryForId(t.getCurrency().get_id()).getExchange();
			} else {
				res += t.getAmount();
			}
		}

		return res * currency.getExchange();
	}

	/**
	 * Creates and returns an instance of the account from the provided JSON data.
	 * 
	 * @param account
	 *            The JSONObject containing account data
	 * @return account The new instance of Voiper user created from the JSON data.
	 */
	@SuppressWarnings("unchecked")
	public static AccountEntity valueOf(Context ctxt, JSONObject account) {
		try {
			String globalId = account.has("global_id") ? account.getString("global_id") : null;
			String name = account.has("name") ? account.getString("name") : null;
			String description = account.has("description") ? account.getString("description") : null;
			String walletGlobalId = account.has("wallet_global_id") ? account.getString("wallet_global_id") : null;
			String accounTypeGlobalId = account.has("account_type_global_id") ? account
					.getString("account_type_global_id") : null;

			Dao<Wallet, Integer> daoWallet = (Dao<Wallet, Integer>) DatabaseHelper.getHelper(ctxt).getMyDao(
					Wallet.class);
			QueryBuilder<Wallet, Integer> qbWallet = daoWallet.queryBuilder();
			qbWallet.where().eq(Wallet.GLOBAL_ID, walletGlobalId);
			Wallet wallet = daoWallet.queryForFirst(qbWallet.prepare());

			Dao<AccountType, Integer> daoAccountType = (Dao<AccountType, Integer>) DatabaseHelper.getHelper(ctxt)
					.getMyDao(AccountType.class);
			QueryBuilder<AccountType, Integer> qbAccountType = daoAccountType.queryBuilder();
			qbAccountType.where().eq(AccountType.GLOBAL_ID, accounTypeGlobalId);
			AccountType accountType = daoAccountType.queryForFirst(qbAccountType.prepare());

			AccountEntity accountEntity = new AccountEntity(name, description, wallet, accountType);
			accountEntity.setGlobal_id(globalId);
			return accountEntity;
		} catch (Exception ex) {
			Log.i("AccountEntity", "Error parsing JSON account object" + ex.toString());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<AccountEntity> dameListadoCuentas(Context ctxt, Wallet walletFather) throws SQLException {
		Dao<AccountEntity, Integer> dao = (Dao<AccountEntity, Integer>) DatabaseHelper.getHelper(ctxt).getMyDao(
				AccountEntity.class);
		QueryBuilder<AccountEntity, Integer> qb = dao.queryBuilder();
		qb.where().eq(AccountEntity.WALLET_ID, walletFather.get_id());
		return dao.query(qb.prepare());
	}
}
