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
import com.senechaux.rutino.db.entities.Wallet;

public class WalletEdit extends OrmLiteBaseActivity<DatabaseHelper> {

	private static final String WALLET_ID = "walletId";

	private EditText walletName;
	private EditText walletDesc;
	private Button createWallet;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.wallet_edit);

		walletName = (EditText) findViewById(R.id.walletName);
		walletDesc = (EditText) findViewById(R.id.walletDesc);
		createWallet = (Button) findViewById(R.id.walletConfirm);

		createWallet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				try {
					Wallet wallet = saveToObj();
					Dao<Wallet, Integer> walletDao = getHelper().getWalletDao();
					boolean alreadyCreated = false;
					if (wallet.getId() != null) {
						Wallet dbWallet = walletDao.queryForId(wallet.getId());
						if (dbWallet != null) {
							walletDao.update(wallet);
							alreadyCreated = true;
						}
					}
					if (alreadyCreated) {
						finish();
					} else {
						walletDao.create(wallet);
						// CounterScreen.callMe(WalletEdit.this, wallet.getId());
						finish();
					}
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		});
		reInit(savedInstanceState);
	}

	public static void callMe(Context c) {
		c.startActivity(new Intent(c, WalletEdit.class));
	}

	public static void callMe(Context c, Integer walletCountId) {
		Intent intent = new Intent(c, WalletEdit.class);
		intent.putExtra(WALLET_ID, walletCountId);
		c.startActivity(intent);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Wallet w = saveToObj();
		outState.putSerializable("WALLET", w);
	}

	private void reInit(Bundle savedInstanceState) {
		try {
			Dao<Wallet, Integer> walletDao = getHelper().getWalletDao();

			if (savedInstanceState != null) {
				loadFromObj((Wallet) savedInstanceState.get(WALLET_ID));
			} else {
				int walletId = getWalletId();

				if (walletId > -1) {
					Wallet wallet = walletDao.queryForId(walletId);
					if (wallet != null) {
						loadFromObj(wallet);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Wallet saveToObj() {
		Wallet c = new Wallet();

		int walletCountId = getWalletId();
		if (walletCountId > -1) {
			c.setId(walletCountId);
		}
		c.setName(walletName.getText().toString());
		c.setDesc(walletDesc.getText().toString());
		return c;
	}

	private int getWalletId() {
		return getIntent().getIntExtra(WALLET_ID, -1);
	}

	private void loadFromObj(Wallet c) throws SQLException {
		walletName.setText(c.getName());
		walletDesc.setText(c.getDesc());
	}

}
