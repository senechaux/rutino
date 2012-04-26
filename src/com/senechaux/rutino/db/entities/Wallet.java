package com.senechaux.rutino.db.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Wallet extends BaseEntity {
	private static final long serialVersionUID = 1L;

    public static final String OBJ = "wallet";
    public static final String NAME = "name";
    public static final String DESC = "desc";

	@DatabaseField(canBeNull = false)
	private String name;
	@DatabaseField
	private String desc;

	public Wallet() {
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

	@Override
	public String toString() {
		return "Wallet [id=" + this.get_id() + ", name=" + name + ", desc=" + desc + "]";
	}

}
