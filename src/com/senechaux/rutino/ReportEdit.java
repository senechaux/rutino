package com.senechaux.rutino;

import java.sql.SQLException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.Report;
import com.senechaux.rutino.db.entities.Wallet;

public class ReportEdit extends OrmLiteBaseActivity<DatabaseHelper> {

	private EditText reportName;
	private EditText reportDesc;
	private Button createReport;
	private Report report;
	private Wallet walletFather;

	public static void callMe(Context c, Wallet wFather) {
		Intent intent = new Intent(c, ReportEdit.class);
		intent.putExtra(Wallet.OBJ, wFather);
		c.startActivity(intent);
	}

	public static void callMe(Context c, Report report) {
		Intent intent = new Intent(c, ReportEdit.class);
		intent.putExtra(Report.OBJ, report);
		c.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.report_edit);

		reportName = (EditText) findViewById(R.id.reportName);
		reportDesc = (EditText) findViewById(R.id.reportDesc);
		createReport = (Button) findViewById(R.id.reportConfirm);

		walletFather = (Wallet) getIntent().getSerializableExtra(Wallet.OBJ);
		report = (Report) getIntent().getSerializableExtra(Report.OBJ);

		createReport.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				try {
					saveToObj();
					getHelper().getReportDao().createOrUpdate(report);
					finish();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		});

		reInit(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveToObj();
		outState.putSerializable(Report.OBJ, report);
	}

	private void reInit(Bundle savedInstanceState) {
		try {
			if (savedInstanceState != null) {
				report = (Report) savedInstanceState.get(Report.OBJ);
			}
			if (report != null) {
				loadFromObj();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void saveToObj() {
		if (report == null)
			report = new Report();
		if (walletFather != null)
			report.setWallet(walletFather);
		report.setName(reportName.getText().toString());
		report.setDesc(reportDesc.getText().toString());
		return;
	}

	private void loadFromObj() throws SQLException {
		reportName.setText(report.getName());
		reportDesc.setText(report.getDesc());
	}

}
