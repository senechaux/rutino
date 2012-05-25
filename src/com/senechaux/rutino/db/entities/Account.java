package com.senechaux.rutino.db.entities;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.senechaux.rutino.db.DatabaseHelper;

@DatabaseTable
public class Account extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public static final String OBJ = "account";
	public static final String NAME = "name";
	public static final String DESC = "desc";
	public static final String WALLET_ID = "wallet_id";
	public static final String ACCOUNTTYPE_ID = "accounttype_id";

	@DatabaseField(canBeNull = false)
	private String name;
	@DatabaseField
	private String desc;
	@DatabaseField(canBeNull = false, foreign = true)
	private Wallet wallet;
	@DatabaseField(canBeNull = false, foreign = true)
	private AccountType accountType;

	public Account() {
		super();
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
		Dao<Transaction, Integer> dao = (Dao<Transaction, Integer>)DatabaseHelper.getHelper(ctxt).getMyDao(Transaction.class);
		QueryBuilder<Transaction, Integer> qb = dao.queryBuilder();
		qb.where().eq(Transaction.ACCOUNT_ID, this.get_id());
		List<Transaction> list = dao.query(qb.prepare());

		Dao<Currency, Integer> daoCurrency = (Dao<Currency, Integer>)DatabaseHelper.getHelper(ctxt).getMyDao(Currency.class);

		Double res = 0.0;
		for (Transaction t : list) {
			if (t.getCurrency().get_id() != 1) {
				res += t.getAmount() / daoCurrency.queryForId(t.getCurrency().get_id()).getChange();
			} else {
				res += t.getAmount();
			}
		}

		return res * currency.getChange();
	}

}
