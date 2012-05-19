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
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String ACCOUNT_ID = "account_id";
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
	protected Account account;
	@DatabaseField(canBeNull = false, foreign = true)
	protected Currency currency;

	public Transaction() {
		super();
	}

	public Transaction(PeriodicTransaction perTrans) {
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
		return "Transaction [id=" + this.get_id() + ", name=" + name + ", desc=" + desc + ", amount=" + amount
				+ ", account=" + account + "]";
	}

}
