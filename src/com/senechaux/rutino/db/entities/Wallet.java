package com.senechaux.rutino.db.entities;

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
			final int mobileId = wallet.has("mobile_id") ? wallet.getInt("mobile_id") : null;
			// final int userId = wallet.has("user_id") ? wallet.getInt("uid") :
			// null;
			final String name = wallet.has("name") ? wallet.getString("name") : null;
			final String description = wallet.has("description") ? wallet.getString("description") : null;
			// final String name = "caca";
			// final String description = "futi";
			Wallet walletEntity = new Wallet(name, description);
			walletEntity.set_id(mobileId);
			return walletEntity;
		} catch (final Exception ex) {
			Log.i("User", "Error parsing JSON user object" + ex.toString());

		}
		return null;

	}

}
