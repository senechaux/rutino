package com.senechaux.rutino;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.AccountEntity;
import com.senechaux.rutino.db.entities.Report;
import com.senechaux.rutino.db.entities.Transaction;

@SuppressWarnings("unchecked")
public class ReportView extends Activity {
	private static final String TAG = "ReportView";

	private Report report;
	private TextView reportName, reportDateFrom, reportDateTo, reportInputs, reportOutputs, reportTotal;

	public static void callMe(Context c, Report report) {
		Intent intent = new Intent(c, ReportView.class);
		intent.putExtra(Report.OBJ, report);
		c.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_view);
		report = (Report) getIntent().getSerializableExtra(Report.OBJ);
		reportName = (TextView) findViewById(R.id.reportName);
		reportDateFrom = (TextView) findViewById(R.id.reportDateFrom);
		reportDateTo = (TextView) findViewById(R.id.reportDateTo);
		reportInputs = (TextView) findViewById(R.id.reportInputs);
		reportOutputs = (TextView) findViewById(R.id.reportOutputs);
		reportTotal = (TextView) findViewById(R.id.reportTotal);

		reInit(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(Report.OBJ, report);
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			fillData();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void reInit(Bundle savedInstanceState) {
		// try {
		if (savedInstanceState != null) {
			report = (Report) savedInstanceState.get(Report.OBJ);
		}
		// if (report != null) {
		// loadFromObj();
		// }
		// } catch (SQLException e) {
		// throw new RuntimeException(e);
		// }
	}

	private void fillData() throws SQLException {
		DateFormat df = DateFormat.getDateInstance();

		reportName.setText(report.getName());
		reportDateFrom.setText(df.format(report.getDateFrom()));
		reportDateTo.setText(df.format(report.getDateTo()));

		Dao<AccountEntity, Integer> dao = (Dao<AccountEntity, Integer>) DatabaseHelper.getHelper(this).getMyDao(
				AccountEntity.class);
		QueryBuilder<AccountEntity, Integer> qb = dao.queryBuilder();
		qb.where().eq(AccountEntity.WALLET_ID, report.getWallet().get_id());
		List<AccountEntity> accountList = dao.query(qb.prepare());

		Dao<Transaction, Integer> daoTrans = (Dao<Transaction, Integer>) DatabaseHelper.getHelper(this).getMyDao(
				Transaction.class);
		QueryBuilder<Transaction, Integer> qbTrans = daoTrans.queryBuilder();
		qbTrans.where().in(Transaction.ACCOUNTENTITY_ID, accountList).and().ge(Transaction.AMOUNT, 0);
		qbTrans.selectRaw("SUM(" + Transaction.AMOUNT + ")");

		GenericRawResults<String[]> results = daoTrans.queryRaw(qbTrans.prepareStatementString());
		String[] suma = results.getFirstResult();
		Double inputs = Double.parseDouble(suma[0]);
		reportInputs.setText(String.valueOf(inputs));

		qbTrans.where().in(Transaction.ACCOUNTENTITY_ID, accountList).and().lt(Transaction.AMOUNT, 0);
		results = daoTrans.queryRaw(qbTrans.prepareStatementString());
		suma = results.getFirstResult();
		Double outputs = Double.parseDouble(suma[0]);
		reportOutputs.setText(String.valueOf(outputs));

		reportTotal.setText(String.valueOf(inputs + outputs));
	}

}
