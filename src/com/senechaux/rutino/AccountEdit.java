package com.senechaux.rutino;

import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.j256.ormlite.dao.Dao;
import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.Account;
import com.senechaux.rutino.db.entities.AccountType;
import com.senechaux.rutino.db.entities.Wallet;

public class AccountEdit extends Activity {

	private EditText accountName;
	private EditText accountDesc;
	private AccountType accountType;
	private Button createAccount;
	private Spinner accountTypeSpinner;
	private Account account;
	private Wallet walletFather;

	public static void callMe(Context c, Wallet wFather) {
		Intent intent = new Intent(c, AccountEdit.class);
		intent.putExtra(Wallet.OBJ, wFather);
		c.startActivity(intent);
	}

	public static void callMe(Context c, Account account) {
		Intent intent = new Intent(c, AccountEdit.class);
		intent.putExtra(Account.OBJ, account);
		c.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.account_edit);

		accountName = (EditText) findViewById(R.id.accountName);
		accountDesc = (EditText) findViewById(R.id.accountDesc);
		accountTypeSpinner = (Spinner) findViewById(R.id.accountType);
		createAccount = (Button) findViewById(R.id.accountConfirm);

		walletFather = (Wallet) getIntent().getSerializableExtra(Wallet.OBJ);
		account = (Account) getIntent().getSerializableExtra(Account.OBJ);

		// Acción del botón Crear
		createAccount.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				try {
					saveToObj();
					DatabaseHelper.getHelper(AccountEdit.this)
							.getAccountDao().createOrUpdate(account);
					finish();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		});

		// Rellenar spinner Currency
		try {
			Dao<AccountType, Integer> dao = DatabaseHelper.getHelper(this)
					.getAccountTypeDao();
			List<AccountType> list = dao.queryForAll();
			final ArrayAdapter<AccountType> adapter = new ArrayAdapter<AccountType>(
					this, android.R.layout.simple_spinner_item, list);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			accountTypeSpinner.setAdapter(adapter);
			accountTypeSpinner
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							accountType = adapter.getItem(position);
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
		outState.putSerializable(Account.OBJ, account);
	}

	private void reInit(Bundle savedInstanceState) {
		try {
			if (savedInstanceState != null) {
				account = (Account) savedInstanceState.get(Account.OBJ);
			}
			if (account != null) {
				loadFromObj();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void saveToObj() {
		if (account == null)
			account = new Account();
		if (walletFather != null)
			account.setWallet(walletFather);
		account.setName(accountName.getText().toString());
		account.setDesc(accountDesc.getText().toString());
		account.setAccountType(accountType);
		return;
	}

	@SuppressWarnings("unchecked")
	private void loadFromObj() throws SQLException {
		accountName.setText(account.getName());
		accountDesc.setText(account.getDesc());

		ArrayAdapter<AccountType> adapter = (ArrayAdapter<AccountType>) accountTypeSpinner
				.getAdapter();
		accountTypeSpinner.setSelection(adapter.getPosition(account
				.getAccountType()));
	}

}
