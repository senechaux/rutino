package com.senechaux.rutino.db.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.senechaux.rutino.utils.UtilDate;

@DatabaseTable
public class PeriodicTransaction extends Transaction {
	private static final long serialVersionUID = 1L;

	public static final String OBJ = "periodicTransaction";

	@DatabaseField
	private int periodicity;

	public PeriodicTransaction() {
		super();
	}

	public PeriodicTransaction(Transaction transaction, int periodicity) {
		this.name = transaction.name;
		this.desc = transaction.desc;
		this.amount = transaction.amount;
		this.date = transaction.date;
		this.account = transaction.account;
		this.currency = transaction.currency;
		this.periodicity = periodicity;
	}

	public int getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(int periodicity) {
		this.periodicity = periodicity;
	}
	
	public void setNextDate() {
		this.setDate(UtilDate.addDays(this.getDate(),
				this.getPeriodicity()));
	}

	@Override
	public String toString() {
		return "PeriodicTransaction [periodicity=" + periodicity + ", name="
				+ name + ", desc=" + desc + ", amount=" + amount + ", date="
				+ date + ", account=" + account + ", currency=" + currency
				+ ", _id=" + _id + "]";
	}

}
