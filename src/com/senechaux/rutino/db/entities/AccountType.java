package com.senechaux.rutino.db.entities;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.senechaux.rutino.db.DatabaseHelper;

@DatabaseTable
public class AccountType extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public static final String OBJ = "accountType";
	public static final String NAME = "name";
	public static final String DESC = "desc";

	@DatabaseField(canBeNull = false)
	private String name;
	@DatabaseField
	private String desc;

	public AccountType() {
		super();
	}

	public AccountType(String name, String desc) {
		super();
		this.name = name;
		this.desc = desc;
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
		return name;
	}

	/**
	 * Creates and returns an instance of the account from the provided JSON data.
	 * 
	 * @param account The JSONObject containing account data
	 * @return account The new instance of Voiper user created from the JSON data.
	 */
	public static AccountType valueOf(Context ctxt, JSONObject accountType) {
		try {
			String globalId = accountType.has("global_id") ? accountType.getString("global_id") : null;
			String name = accountType.has("name") ? accountType.getString("name") : null;
			String description = accountType.has("description") ? accountType.getString("description") : null;
			AccountType accountTypeEntity = new AccountType(name, description);
			accountTypeEntity.setGlobal_id(globalId);
			return accountTypeEntity;
		} catch (Exception ex) {
			Log.i("AccountType", "Error parsing JSON AccountType object" + ex.toString());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<AccountType> getAccountTypeList(Context ctxt) throws SQLException {
		Dao<AccountType, Integer> dao = (Dao<AccountType, Integer>) DatabaseHelper.getHelper(ctxt).getMyDao(
				AccountType.class);
		return dao.queryForAll();
	}

}
