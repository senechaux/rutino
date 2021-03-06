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

import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.AccountEntity;
import com.senechaux.rutino.db.entities.AccountType;
import com.senechaux.rutino.db.entities.Wallet;

@SuppressWarnings("unchecked")
public class AccountEdit extends Activity {
	private static final String TAG = "AccountEdit";

	private EditText accountName;
	private EditText accountDesc;
	private AccountType accountType;
	private Button createAccount;
	private Spinner accountTypeSpinner;
	private AccountEntity accountEntity;
	private Wallet walletFather;

	public static void callMe(Context c, Wallet wFather) {
		Intent intent = new Intent(c, AccountEdit.class);
		intent.putExtra(Wallet.OBJ, wFather);
		c.startActivity(intent);
	}

	public static void callMe(Context c, AccountEntity accountEntity) {
		Intent intent = new Intent(c, AccountEdit.class);
		intent.putExtra(AccountEntity.OBJ, accountEntity);
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
		accountEntity = (AccountEntity) getIntent().getSerializableExtra(AccountEntity.OBJ);

		// Acción del botón Crear
		createAccount.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				try {
					saveToObj();
					DatabaseHelper.getHelper(AccountEdit.this).genericCreateOrUpdate(AccountEdit.this, accountEntity);
					finish();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		});

		try {
			List<AccountType> list = AccountType.getAccountTypeList(AccountEdit.this);
			final ArrayAdapter<AccountType> adapter = new ArrayAdapter<AccountType>(this,
					android.R.layout.simple_spinner_item, list);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			accountTypeSpinner.setAdapter(adapter);
			accountTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
		outState.putSerializable(AccountEntity.OBJ, accountEntity);
	}

	private void reInit(Bundle savedInstanceState) {
		try {
			if (savedInstanceState != null) {
				accountEntity = (AccountEntity) savedInstanceState.get(AccountEntity.OBJ);
			}
			if (accountEntity != null) {
				loadFromObj();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void saveToObj() {
		if (accountEntity == null)
			accountEntity = new AccountEntity();
		if (walletFather != null)
			accountEntity.setWallet(walletFather);
		accountEntity.setName(accountName.getText().toString());
		accountEntity.setDesc(accountDesc.getText().toString());
		accountEntity.setAccountType(accountType);
		return;
	}

	private void loadFromObj() throws SQLException {
		accountName.setText(accountEntity.getName());
		accountDesc.setText(accountEntity.getDesc());

		ArrayAdapter<AccountType> adapter = (ArrayAdapter<AccountType>) accountTypeSpinner.getAdapter();
		accountTypeSpinner.setSelection(adapter.getPosition(accountEntity.getAccountType()));
	}

}
