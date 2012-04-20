package com.senechaux.rutino.db.entities;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Account implements Serializable {
	private static final long serialVersionUID = 8818426783186993288L;
	
    public static final String OBJ = "account";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESC = "desc";
    public static final String WALLET_ID = "wallet_id";

	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField
	String name;
	@DatabaseField
	String desc;
	@DatabaseField(canBeNull = false, foreign = true)
	Wallet wallet;
	

	public Account() {
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

	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", name=" + name + ", desc=" + desc
				+ ", wallet=" + wallet + "]";
	}

}
