package com.senechaux.rutino;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.Report;
import com.senechaux.rutino.db.entities.Wallet;
import com.senechaux.rutino.utils.StringUtils;

@SuppressWarnings("unchecked")
public class ReportEdit extends Activity {
	private static final String TAG = "ReportEdit"; 
	protected static final int DATE_FROM_DIALOG_ID = 0;
	protected static final int DATE_TO_DIALOG_ID = 1;
	protected static final int DATE_VALIDA_GREATER = 2;

	private EditText reportName, reportDesc;
	private Button createReport, mPickDateFrom, mPickDateTo;
	private GregorianCalendar reportDateTimeFrom, reportDateTimeTo;
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
		mPickDateFrom = (Button) findViewById(R.id.reportDateFrom);
		mPickDateTo = (Button) findViewById(R.id.reportDateTo);
		walletFather = (Wallet) getIntent().getSerializableExtra(Wallet.OBJ);
		report = (Report) getIntent().getSerializableExtra(Report.OBJ);

		mPickDateFrom.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_FROM_DIALOG_ID);
			}
		});

		mPickDateTo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_TO_DIALOG_ID);
			}
		});

		createReport.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (reportDateTimeFrom.after(reportDateTimeTo)) {
					showDialog(DATE_VALIDA_GREATER);
				} else {
					try {
						saveToObj();
						DatabaseHelper.getHelper(ReportEdit.this).genericCreateOrUpdate(ReportEdit.this, report);
						finish();
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				}
			}
		});

		reportDateTimeFrom = new GregorianCalendar();
		reportDateTimeFrom.set(GregorianCalendar.HOUR_OF_DAY, 0);
		reportDateTimeFrom.set(GregorianCalendar.MINUTE, 0);
		StringUtils.updateTimeDate(mPickDateFrom, null, reportDateTimeFrom);

		reportDateTimeTo = new GregorianCalendar();
		reportDateTimeTo.set(GregorianCalendar.HOUR_OF_DAY, 23);
		reportDateTimeTo.set(GregorianCalendar.MINUTE, 59);
		StringUtils.updateTimeDate(mPickDateTo, null, reportDateTimeTo);

		reInit(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveToObj();
		outState.putSerializable(Report.OBJ, report);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		int mYear, mMonth, mDay;
		switch (id) {
		case DATE_FROM_DIALOG_ID:
			mYear = reportDateTimeFrom.get(Calendar.YEAR);
			mMonth = reportDateTimeFrom.get(Calendar.MONTH);
			mDay = reportDateTimeFrom.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(this, mDateFromSetListener, mYear, mMonth, mDay);
		case DATE_TO_DIALOG_ID:
			mYear = reportDateTimeTo.get(Calendar.YEAR);
			mMonth = reportDateTimeTo.get(Calendar.MONTH);
			mDay = reportDateTimeTo.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(this, mDateToSetListener, mYear, mMonth, mDay);
		case DATE_VALIDA_GREATER:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.report_date_validation_dialog).setCancelable(false)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			return alert;
		}
		return null;
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
		report.setDateFrom(reportDateTimeFrom.getTime());
		report.setDateTo(reportDateTimeTo.getTime());
		return;
	}

	private void loadFromObj() throws SQLException {
		reportName.setText(report.getName());
		reportDesc.setText(report.getDesc());

		reportDateTimeFrom.setTime(report.getDateFrom());
		StringUtils.updateTimeDate(mPickDateFrom, null, reportDateTimeFrom);
		reportDateTimeTo.setTime(report.getDateTo());
		StringUtils.updateTimeDate(mPickDateTo, null, reportDateTimeTo);
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateFromSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			reportDateTimeFrom.set(GregorianCalendar.YEAR, year);
			reportDateTimeFrom.set(GregorianCalendar.MONTH, monthOfYear);
			reportDateTimeFrom.set(GregorianCalendar.DAY_OF_MONTH, dayOfMonth);
			StringUtils.updateTimeDate(mPickDateFrom, null, reportDateTimeFrom);
		}
	};

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateToSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			reportDateTimeTo.set(GregorianCalendar.YEAR, year);
			reportDateTimeTo.set(GregorianCalendar.MONTH, monthOfYear);
			reportDateTimeTo.set(GregorianCalendar.DAY_OF_MONTH, dayOfMonth);
			StringUtils.updateTimeDate(mPickDateTo, null, reportDateTimeTo);
		}
	};

}
