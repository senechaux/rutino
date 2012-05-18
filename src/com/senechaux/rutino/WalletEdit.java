package com.senechaux.rutino;

import java.sql.SQLException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.Wallet;

public class WalletEdit extends Activity {

	private EditText walletName;
	private EditText walletDesc;
	private Button createWallet;
	private Wallet wallet;

	public static void callMe(Context c) {
		c.startActivity(new Intent(c, WalletEdit.class));
	}

	public static void callMe(Context c, Wallet wallet) {
		Intent intent = new Intent(c, WalletEdit.class);
		intent.putExtra(Wallet.OBJ, wallet);
		c.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.wallet_edit);

		walletName = (EditText) findViewById(R.id.walletName);
		walletDesc = (EditText) findViewById(R.id.walletDesc);
		createWallet = (Button) findViewById(R.id.walletConfirm);
		wallet = (Wallet) getIntent().getSerializableExtra(Wallet.OBJ);

		createWallet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				try {
					saveToObj();
					DatabaseHelper.getHelper(WalletEdit.this).getWalletDao().createOrUpdate(wallet);
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
		outState.putSerializable(Wallet.OBJ, wallet);
	}

	private void reInit(Bundle savedInstanceState) {
		try {
			if (savedInstanceState != null) {
				wallet = (Wallet) savedInstanceState.get(Wallet.OBJ);
			}
			if (wallet != null) {
				loadFromObj();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void saveToObj() {
		if (wallet == null)
			wallet = new Wallet();
		wallet.setName(walletName.getText().toString());
		wallet.setDesc(walletDesc.getText().toString());
		return;
	}

	private void loadFromObj() throws SQLException {
		walletName.setText(wallet.getName());
		walletDesc.setText(wallet.getDesc());
	}

}
