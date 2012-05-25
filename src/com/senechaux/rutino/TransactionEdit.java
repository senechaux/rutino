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
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.j256.ormlite.dao.Dao;
import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.AccountEntity;
import com.senechaux.rutino.db.entities.Currency;
import com.senechaux.rutino.db.entities.PeriodicTransaction;
import com.senechaux.rutino.db.entities.Transaction;
import com.senechaux.rutino.utils.AlarmUtils;
import com.senechaux.rutino.utils.GeoUtils;
import com.senechaux.rutino.utils.StringUtils;

@SuppressWarnings("unchecked")
public class TransactionEdit extends Activity {
	private static final String TAG = "TransactionEdit";
	protected static final int DATE_DIALOG_ID = 0;
	protected static final int TIME_DIALOG_ID = 1;

	private EditText transactionName;
	private EditText transactionDesc;
	private EditText transactionAmount;
	private EditText periodicity;
	private Spinner currencySpinner;
	private Button mPickDate;
	private Button mPickTime;
	private CheckBox isPeriodic;
	private CheckBox geotag;
	private Button createTransaction;
	private GregorianCalendar transactionDateTime;
	private Currency currency;
	private Transaction transaction;
	private AccountEntity accountFather;
	private Double latitude, longitude;
	private TextView latitudeText, longitudeText;

	public static void callMe(Context c, AccountEntity aFather) {
		Intent intent = new Intent(c, TransactionEdit.class);
		intent.putExtra(AccountEntity.OBJ, aFather);
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
		geotag = (CheckBox) findViewById(R.id.geotag);
		latitudeText = (TextView) findViewById(R.id.latitude);
		longitudeText = (TextView) findViewById(R.id.longitude);

		accountFather = (AccountEntity) getIntent().getSerializableExtra(AccountEntity.OBJ);
		transaction = (Transaction) getIntent().getSerializableExtra(Transaction.OBJ);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

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

		geotag.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Location l = GeoUtils.getLocation(TransactionEdit.this);
					latitude = l.getLatitude();
					longitude = l.getLongitude();
					latitudeText.setText(getString(R.string.latitude) + " " + String.valueOf(latitude));
					longitudeText.setText(getString(R.string.longitude) + " " + String.valueOf(longitude));
				}
			}
		});

		createTransaction.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				try {
					saveToObj();
					if (!isPeriodic.isChecked()) {
						DatabaseHelper.getHelper(TransactionEdit.this).genericCreateOrUpdate(TransactionEdit.this, transaction);
					} else {
						insertPeriodic();
					}
					finish();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		});

		transactionDateTime = new GregorianCalendar();
		StringUtils.updateTimeDate(mPickDate, mPickTime, transactionDateTime);

		try {
			Dao<Currency, Integer> dao = (Dao<Currency, Integer>) DatabaseHelper.getHelper(this).getMyDao(
					Currency.class);
			List<Currency> list = dao.queryForAll();
			final ArrayAdapter<Currency> adapter = new ArrayAdapter<Currency>(this,
					android.R.layout.simple_spinner_item, list);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			currencySpinner.setAdapter(adapter);
			currencySpinner.setSelection(adapter.getPosition(dao.queryForId(Integer.valueOf(prefs.getString("currency",
					"1")))));
			currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		case TIME_DIALOG_ID:
			int mHour = transactionDateTime.get(Calendar.HOUR_OF_DAY);
			int mMinute = transactionDateTime.get(Calendar.MINUTE);
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, true);
		}
		return null;
	}

	private void reInit(Bundle savedInstanceState) {
		try {
			if (savedInstanceState != null) {
				transaction = (Transaction) savedInstanceState.get(Transaction.OBJ);
			}
			if (transaction != null) {
				loadFromObj();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void saveToObj() {
		if (transaction == null) {
			if (isPeriodic.isChecked())
				transaction = new PeriodicTransaction();
			else
				transaction = new Transaction();
		}
		transaction.setAccount(accountFather);

		transaction.setName(transactionName.getText().toString());
		transaction.setDesc(transactionDesc.getText().toString());
		transaction.setCurrency(currency);

		String amount = transactionAmount.getText().toString();
		if (amount.trim().equals(""))
			transaction.setAmount(0.0);
		else
			transaction.setAmount(Double.parseDouble(amount));

		if (geotag.isChecked()) {
			transaction.setLatitude(latitude);
			transaction.setLongitude(longitude);
		}

		if (isPeriodic.isChecked()) {
			String p = periodicity.getText().toString();
			if (p.trim().equals(""))
				((PeriodicTransaction) transaction).setPeriodicity(1);
			else
				((PeriodicTransaction) transaction).setPeriodicity(Integer.parseInt(p));
		}

		transaction.setDate(transactionDateTime.getTime());
		return;
	}

	private void loadFromObj() throws SQLException {
		accountFather = transaction.getAccount();
		transactionName.setText(transaction.getName());
		transactionDesc.setText(transaction.getDesc());
		transactionAmount.setText(transaction.getAmount().toString());

		transactionDateTime.setTime(transaction.getDate());
		StringUtils.updateTimeDate(mPickDate, mPickTime, transactionDateTime);

		ArrayAdapter<Currency> adapter = (ArrayAdapter<Currency>) currencySpinner.getAdapter();
		currencySpinner.setSelection(adapter.getPosition(transaction.getCurrency()));
		if (transaction.getLatitude() != null) {
			latitude = transaction.getLatitude();
			longitude = transaction.getLongitude();
			latitudeText.setText(getString(R.string.latitude) + " " + String.valueOf(latitude));
			longitudeText.setText(getString(R.string.longitude) + " " + String.valueOf(longitude));
		}

		if (transaction.getClass() == PeriodicTransaction.class) {
			isPeriodic.setChecked(true);
			isPeriodic.setEnabled(false);
			periodicity.setText(String.valueOf(((PeriodicTransaction) transaction).getPeriodicity()));
			periodicity.setEnabled(false);
		}
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			transactionDateTime.set(GregorianCalendar.YEAR, year);
			transactionDateTime.set(GregorianCalendar.MONTH, monthOfYear);
			transactionDateTime.set(GregorianCalendar.DAY_OF_MONTH, dayOfMonth);
			StringUtils.updateTimeDate(mPickDate, mPickTime, transactionDateTime);
		}
	};

	// the callback received when the user "sets" the time in the dialog
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			transactionDateTime.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
			transactionDateTime.set(GregorianCalendar.MINUTE, minute);
			StringUtils.updateTimeDate(mPickDate, mPickTime, transactionDateTime);
		}
	};

	private void insertPeriodic() throws SQLException {
		PeriodicTransaction perTrans = (PeriodicTransaction) transaction;
		// Si existe se borra la anterior alarma
		if (perTrans.get_id() != null) {
			AlarmUtils.cancelAlarm(this, perTrans);
		}
		// Inserta en BBDD
		DatabaseHelper.getHelper(TransactionEdit.this).genericCreateOrUpdate(TransactionEdit.this, perTrans);

		AlarmUtils.setAlarm(this, perTrans);
	}

}
