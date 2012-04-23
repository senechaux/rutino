package com.senechaux.rutino.db.entities;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Transaction implements Serializable {
	private static final long serialVersionUID = 8818426783186993288L;
	
    public static final String OBJ = "transaction";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String AMOUNT = "amount";
    public static final String DESC = "desc";
    public static final String ACCOUNT_ID = "account_id";

	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField
	private String name;
	@DatabaseField
	private String desc;
	@DatabaseField
	private Double amount;
	@DatabaseField(canBeNull = false, foreign = true)
	private Account account;
	

	public Transaction() {
		super();
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
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


	public Account getAccount() {
		return account;
	}


	public void setAccount(Account account) {
		this.account = account;
	}


	@Override
	public String toString() {
		return "Transaction [id=" + id + ", name=" + name + ", desc=" + desc
				+ ", amount=" + amount + ", account=" + account + "]";
	}

}
