package com.senechaux.rutino.db.entities;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Report extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public static final String OBJ = "report";
	public static final String NAME = "name";
	public static final String DESC = "desc";
	public static final String DATEFROM = "dateFrom";
	public static final String DATETO = "dateTo";
	public static final String WALLET_ID = "wallet_id";

	@DatabaseField(canBeNull = false)
	private String name;
	@DatabaseField
	private String desc;
	@DatabaseField
	private Date dateFrom;
	@DatabaseField
	private Date dateTo;
	@DatabaseField(canBeNull = false, foreign = true)
	private Wallet wallet;

	public Report() {
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

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	@Override
	public String toString() {
		return "Report [id=" + this.get_id() + ", name=" + name + ", desc="
				+ desc + ", wallet=" + wallet + "]";
	}

}
