package com.senechaux.rutino;

import java.sql.SQLException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.Account;
import com.senechaux.rutino.db.entities.Wallet;

public class AccountEdit extends OrmLiteBaseActivity<DatabaseHelper> {

	private EditText accountName;
	private EditText accountDesc;
	private Button createAccount;
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
		createAccount = (Button) findViewById(R.id.accountConfirm);
		walletFather = (Wallet) getIntent().getSerializableExtra(Wallet.OBJ);
		account = (Account) getIntent().getSerializableExtra(Account.OBJ);

		createAccount.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				try {
					saveToObj();
					Dao<Account, Integer> accountDao = getHelper().getAccountDao();
					boolean alreadyCreated = false;
					if (account.getId() != null) {
						Account dbAccount = accountDao.queryForId(account.getId());
						if (dbAccount != null) {
							accountDao.update(account);
							alreadyCreated = true;
						}
					}
					if (alreadyCreated) {
						finish();
					} else {
						account.setWallet(walletFather);
						accountDao.create(account);
						finish();
					}
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
		account.setName(accountName.getText().toString());
		account.setDesc(accountDesc.getText().toString());
		return;
	}

	private void loadFromObj() throws SQLException {
		accountName.setText(account.getName());
		accountDesc.setText(account.getDesc());
	}

}
