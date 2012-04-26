package com.senechaux.rutino.db.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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
		return "Account [id=" + this.get_id() + ", name=" + name + ", desc=" + desc
				+ ", wallet=" + wallet + "]";
	}

}
