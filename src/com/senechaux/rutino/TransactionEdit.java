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
import com.senechaux.rutino.db.entities.Transaction;
import com.senechaux.rutino.db.entities.Account;

public class TransactionEdit extends OrmLiteBaseActivity<DatabaseHelper> {

	private EditText transactionName;
	private EditText transactionDesc;
	private EditText transactionAmount;
	private Button createTransaction;
	private Transaction transaction;
	private Account accountFather;

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
		createTransaction = (Button) findViewById(R.id.transactionConfirm);

		accountFather = (Account) getIntent().getSerializableExtra(Account.OBJ);
		transaction = (Transaction) getIntent().getSerializableExtra(
				Transaction.OBJ);

		createTransaction.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				try {
					saveToObj();
					Dao<Transaction, Integer> transactionDao = getHelper()
							.getTransactionDao();
					boolean alreadyCreated = false;
					if (transaction.getId() != null) {
						Transaction dbTransaction = transactionDao
								.queryForId(transaction.getId());
						if (dbTransaction != null) {
							transactionDao.update(transaction);
							alreadyCreated = true;
						}
					}
					if (alreadyCreated) {
						finish();
					} else {
						transaction.setAccount(accountFather);
						transactionDao.create(transaction);
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
		outState.putSerializable(Transaction.OBJ, transaction);
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
		transaction.setName(transactionName.getText().toString());
		transaction.setDesc(transactionDesc.getText().toString());
		String amount = transactionAmount.getText().toString();
		if (amount.trim().equals(""))
			transaction.setAmount(0.0);
		else 
			transaction.setAmount(Double.parseDouble(amount));
		return;
	}

	private void loadFromObj() throws SQLException {
		transactionName.setText(transaction.getName());
		transactionDesc.setText(transaction.getDesc());
		transactionAmount.setText(transaction.getAmount().toString());
	}
}
