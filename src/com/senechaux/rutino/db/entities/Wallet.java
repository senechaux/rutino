package com.senechaux.rutino.db.entities;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Wallet implements Serializable {
	private static final long serialVersionUID = 2033099084510674771L;

	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField
	String name;
	@DatabaseField
	String desc;

	public Wallet() {
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

	@Override
	public String toString() {
		return "Wallet [id=" + id + ", name=" + name + ", desc=" + desc + "]";
	}

}
