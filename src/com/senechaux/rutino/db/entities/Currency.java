package com.senechaux.rutino.db.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Currency extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public static final String OBJ = "currency";
	public static final String NAME = "name";
	public static final String DESC = "desc";
	public static final String CHANGE = "change";

	@DatabaseField(canBeNull = false)
	private String name;
	@DatabaseField
	private String desc;
	@DatabaseField
	private Double change;

	public Currency() {
		super();
	}

	public Currency(String name, String desc, Double change) {
		super();
		this.name = name;
		this.desc = desc;
		this.change = change;
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

	public Double getChange() {
		return change;
	}

	public void setChange(Double change) {
		this.change = change;
	}

	@Override
	public String toString() {
		return name;
	}

}
