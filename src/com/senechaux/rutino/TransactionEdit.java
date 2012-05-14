package com.senechaux.rutino;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.j256.ormlite.dao.Dao;
import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.Account;
import com.senechaux.rutino.db.entities.Currency;
import com.senechaux.rutino.db.entities.PeriodicTransaction;
import com.senechaux.rutino.db.entities.Transaction;

public class TransactionEdit extends Activity {

	private EditText transactionName;
	private EditText transactionDesc;
	private EditText transactionAmount;
	private EditText periodicity;
	private Spinner currencySpinner;
	private Button mPickDate;
	private Button mPickTime;
	private CheckBox isPeriodic;
	private Button createTransaction;
	private GregorianCalendar transactionDateTime;
	private Currency currency;
	private Transaction transaction;
	private Account accountFather;

	protected static final int DATE_DIALOG_ID = 0;
	protected static final int TIME_DIALOG_ID = 1;

	public static void callMe(Context c, Account aFather) {
		Intent intent = new Intent(c, TransactionEdit.class);
		intent.putExtra(Account.OBJ, aFather);
		c.startActivity(intent);
	}

	public static void callMe(Context c, Transaction transaction) {
		Intent intent = new Intent(c, TransactionEdit.class);
		intent.putExtra(Transaction.OBJ, transaction);
		c.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.transaction_edit);

		transactionName = (EditText) findViewById(R.id.transactionName);
		transactionDesc = (EditText) findViewById(R.id.transactionDesc);
		transactionAmount = (EditText) findViewById(R.id.transactionAmount);
		periodicity = (EditText) findViewById(R.id.periodicity);
		currencySpinner = (Spinner) findViewById(R.id.transactionCurrency);
		createTransaction = (Button) findViewById(R.id.transactionConfirm);
		mPickDate = (Button) findViewById(R.id.transactionDate);
		mPickTime = (Button) findViewById(R.id.transactionTime);
		isPeriodic = (CheckBox) findViewById(R.id.isPeriodic);

		accountFather = (Account) getIntent().getSerializableExtra(Account.OBJ);
		transaction = (Transaction) getIntent().getSerializableExtra(
				Transaction.OBJ);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		mPickDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		mPickTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});

		createTransaction.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				try {
					saveToObj();
					if (!isPeriodic.isChecked())
						DatabaseHelper.getInstance(TransactionEdit.this)
								.getTransactionDao()
								.createOrUpdate(transaction);
					else {
						insertPeriodic();
					}
					finish();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		});

		transactionDateTime = new GregorianCalendar();
		updateTimeDate();

		try {
			Dao<Currency, Integer> dao = DatabaseHelper.getInstance(this)
					.getCurrencyDao();
			List<Currency> list = dao.queryForAll();
			final ArrayAdapter<Currency> adapter = new ArrayAdapter<Currency>(
					this, android.R.layout.simple_spinner_item, list);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			currencySpinner.setAdapter(adapter);
			currencySpinner.setSelection(adapter.getPosition(dao
					.queryForId(Integer.valueOf(prefs
							.getString("currency", "1")))));
			currencySpinner
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							currency = adapter.getItem(position);
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub
						}
					});
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		reInit(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveToObj();
		outState.putSerializable(Transaction.OBJ, transaction);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			int mYear = transactionDateTime.get(Calendar.YEAR);
			int mMonth = transactionDateTime.get(Calendar.MONTH);
			int mDay = transactionDateTime.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		case TIME_DIALOG_ID:
			int mHour = transactionDateTime.get(Calendar.HOUR_OF_DAY);
			int mMinute = transactionDateTime.get(Calendar.MINUTE);
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
					true);
		}
		return null;
	}

	private void reInit(Bundle savedInstanceState) {
		try {
			if (savedInstanceState != null) {
				transaction = (Transaction) savedInstanceState
						.get(Transaction.OBJ);
			}
			if (transaction != null) {
				loadFromObj();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void saveToObj() {
		if (transaction == null)
			transaction = new Transaction();
		if (accountFather != null)
			transaction.setAccount(accountFather);

		transaction.setName(transactionName.getText().toString());
		transaction.setDesc(transactionDesc.getText().toString());
		transaction.setCurrency(currency);
		String amount = transactionAmount.getText().toString();
		if (amount.trim().equals(""))
			transaction.setAmount(0.0);
		else
			transaction.setAmount(Double.parseDouble(amount));

		transaction.setDate(transactionDateTime.getTime());
		return;
	}

	@SuppressWarnings("unchecked")
	private void loadFromObj() throws SQLException {
		transactionName.setText(transaction.getName());
		transactionDesc.setText(transaction.getDesc());
		transactionAmount.setText(transaction.getAmount().toString());

		transactionDateTime.setTime(transaction.getDate());
		updateTimeDate();

		ArrayAdapter<Currency> adapter = (ArrayAdapter<Currency>) currencySpinner
				.getAdapter();
		currencySpinner.setSelection(adapter.getPosition(transaction
				.getCurrency()));
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			transactionDateTime.set(GregorianCalendar.YEAR, year);
			transactionDateTime.set(GregorianCalendar.MONTH, monthOfYear);
			transactionDateTime.set(GregorianCalendar.DAY_OF_MONTH, dayOfMonth);
			updateTimeDate();
		}
	};

	// the callback received when the user "sets" the time in the dialog
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			transactionDateTime.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
			transactionDateTime.set(GregorianCalendar.MINUTE, minute);
			updateTimeDate();
		}
	};

	private void updateTimeDate() {
		int mYear = transactionDateTime.get(Calendar.YEAR);
		int mMonth = transactionDateTime.get(Calendar.MONTH);
		int mDay = transactionDateTime.get(Calendar.DAY_OF_MONTH);
		mPickDate.setText("" + mDay + "-" + (mMonth + 1) + "-" + mYear);

		int mHour = transactionDateTime.get(Calendar.HOUR_OF_DAY);
		int mMinute = transactionDateTime.get(Calendar.MINUTE);
		mPickTime.setText("" + pad(mHour) + ":" + pad(mMinute));
	}

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	private void insertPeriodic() throws SQLException {
		String per = periodicity.getText().toString();
		PeriodicTransaction perTransaction = new PeriodicTransaction(
				transaction, Integer.valueOf(per));
		DatabaseHelper.getInstance(this).getPeriodicTransactionDao()
				.createOrUpdate(perTransaction);

	}

	// private void setAlarm() {
	// Intent intent = new Intent(this, TransactionEdit.class);
	// intent.putExtra(TasksDbAdapter.KEY_ROWID, mRowId);
	// PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
	// intent, PendingIntent.FLAG_ONE_SHOT);
	// am.set(AlarmManager.RTC_WAKEUP, dateTimeTask.getTimeInMillis(),
	// pendingIntent);
	// }

}
