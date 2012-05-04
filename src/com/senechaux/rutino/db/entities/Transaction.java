package com.senechaux.rutino.db.entities;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Transaction extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public static final String OBJ = "transaction";
	public static final String NAME = "name";
	public static final String DESC = "desc";
	public static final String AMOUNT = "amount";
	public static final String DATE = "date";
	public static final String ACCOUNT_ID = "account_id";
	public static final String CURRENCY_ID = "currency_id";

	@DatabaseField
	private String name;
	@DatabaseField
	private String desc;
	@DatabaseField
	private Double amount;
	@DatabaseField
	private Date date;
	@DatabaseField(canBeNull = false, foreign = true)
	private Account account;
	@DatabaseField(canBeNull = false, foreign = true)
	private Currency currency;

	public Transaction() {
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return "Transaction [id=" + this.get_id() + ", name=" + name + ", desc=" + desc
				+ ", amount=" + amount + ", account=" + account + "]";
	}

}
