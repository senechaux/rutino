package com.senechaux.rutino.db.entities;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

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

	/**
	 * Creates and returns an instance of the account from the provided JSON data.
	 * 
	 * @param account The JSONObject containing account data
	 * @return account The new instance of Voiper user created from the JSON data.
	 */
	public static Currency valueOf(Context ctxt, JSONObject currency) {
		try {
			String globalId = currency.has("global_id") ? currency.getString("global_id") : null;
			String name = currency.has("name") ? currency.getString("name") : null;
			String description = currency.has("description") ? currency.getString("description") : null;
			String change = currency.has("change") ? currency.getString("change") : null;
			Currency currencyEntity = new Currency(name, description, Double.parseDouble(change));
			currencyEntity.setGlobal_id(globalId);
			return currencyEntity;
		} catch (Exception ex) {
			Log.i("Wallet", "Error parsing JSON AccountType object" + ex.toString());
		}
		return null;
	}

}
