package com.senechaux.rutino.db.entities;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

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
	 * Creates and returns an instance of the wallet from the provided JSON
	 * data.
	 * 
	 * @param wallet
	 *            The JSONObject containing wallet data
	 * @return wallet The new instance of Voiper user created from the JSON
	 *         data.
	 */
	public static Wallet valueOf(JSONObject wallet) {
		try {
			String globalId = wallet.has("global_id") ? wallet.getString("global_id") : null;
			String name = wallet.has("name") ? wallet.getString("name") : null;
			String description = wallet.has("description") ? wallet.getString("description") : null;
			Wallet walletEntity = new Wallet(name, description);
			walletEntity.setGlobal_id(globalId);
			return walletEntity;
		} catch (JSONException ex) {
			Log.i("User", "Error parsing JSON user object" + ex.toString());

		}
		return null;

	}

}
