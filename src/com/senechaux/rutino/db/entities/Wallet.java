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

	public Wallet(String name, String desc) {
		super();
		this.setName(name);
		this.setDesc(desc);
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

	/**
	 * Creates and returns an instance of the account from the provided JSON data.
	 * 
	 * @param account The JSONObject containing account data
	 * @return account The new instance of Voiper user created from the JSON data.
	 */
	public static Wallet valueOf(Context ctxt, JSONObject wallet) {
		try {
			String globalId = wallet.has("global_id") ? wallet.getString("global_id") : null;
			String name = wallet.has("name") ? wallet.getString("name") : null;
			String description = wallet.has("description") ? wallet.getString("description") : null;
			Wallet walletEntity = new Wallet(name, description);
			walletEntity.setGlobal_id(globalId);
			return walletEntity;
		} catch (Exception ex) {
			Log.i("Wallet", "Error parsing JSON wallet object" + ex.toString());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Wallet> getWalletList(Context ctxt) throws SQLException {
		Dao<Wallet, Integer> dao = (Dao<Wallet, Integer>) DatabaseHelper.getHelper(ctxt).getMyDao(Wallet.class);
		return dao.queryForAll();
	}

}
