package com.senechaux.rutino.db.entities;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.senechaux.rutino.Constants;
import com.senechaux.rutino.db.DatabaseHelper;

@DatabaseTable
public class Report extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public static final String OBJ = "report";
	public static final String NAME = "name";
	public static final String DESC = "desc";
	public static final String DATEFROM = "dateFrom";
	public static final String DATETO = "dateTo";
	public static final String WALLET_ID = "wallet_id";

	@DatabaseField(canBeNull = false)
	private String name;
	@DatabaseField
	private String desc;
	@DatabaseField
	private Date dateFrom;
	@DatabaseField
	private Date dateTo;
	@DatabaseField(canBeNull = false, foreign = true)
	private Wallet wallet;

	public Report() {
		super();
	}

	public Report(String name, String desc, Date dateFrom, Date dateTo, Wallet wallet) {
		super();
		this.name = name;
		this.desc = desc;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.wallet = wallet;
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

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	@Override
	public String toString() {
		return "Report [id=" + this.get_id() + ", name=" + name + ", desc=" + desc + ", wallet=" + wallet + "]";
	}
	
	/**
	 * Creates and returns an instance of the account from the provided JSON data.
	 * 
	 * @param account The JSONObject containing account data
	 * @return account The new instance of Voiper user created from the JSON data.
	 */
	@SuppressWarnings("unchecked")
	public static Report valueOf(Context ctxt, JSONObject report) {
		try {
			String globalId = report.has("global_id") ? report.getString("global_id") : null;
			String name = report.has("name") ? report.getString("name") : null;
			String description = report.has("description") ? report.getString("description") : null;
			String dateFrom = report.has("date_from") ? report.getString("date_from") : null;
			String dateTo = report.has("date_to") ? report.getString("date_to") : null;
			String walletGlobalId = report.has("wallet_global_id") ? report.getString("wallet_global_id") : null;

			Dao<Wallet, Integer> daoWallet = (Dao<Wallet, Integer>) DatabaseHelper.getHelper(ctxt).getMyDao(
					Wallet.class);
			QueryBuilder<Wallet, Integer> qbWallet = daoWallet.queryBuilder();
			qbWallet.where().eq(Wallet.GLOBAL_ID, walletGlobalId);
			Wallet wallet = daoWallet.queryForFirst(qbWallet.prepare());
			
			DateFormat df = new SimpleDateFormat(Constants.API_DATE_FORMAT);
			Report reportEntity = new Report(name, description, df.parse(dateFrom), df.parse(dateTo), wallet);
			reportEntity.setGlobal_id(globalId);
			return reportEntity;
		} catch (Exception ex) {
			Log.i("Report", "Error parsing JSON Report object" + ex.toString());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Report> dameListadoReportes(Context ctxt, Wallet walletFather) throws SQLException {
		Dao<Report, Integer> dao = (Dao<Report, Integer>) DatabaseHelper.getHelper(ctxt).getMyDao(Report.class);
		QueryBuilder<Report, Integer> qb = dao.queryBuilder();
		qb.where().eq(Report.WALLET_ID, walletFather.get_id());
		return dao.query(qb.prepare());
	}

}
