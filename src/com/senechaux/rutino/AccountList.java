package com.senechaux.rutino;

import java.sql.SQLException;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.Account;
import com.senechaux.rutino.db.entities.Currency;
import com.senechaux.rutino.db.entities.Wallet;

public class AccountList extends ListActivity {
	private static final String TAG = "AccountList"; 
	private Wallet walletFather;
	private SharedPreferences prefs;

	public static void callMe(Context c, Wallet wallet) {
		Intent intent = new Intent(c, AccountList.class);
		intent.putExtra(Wallet.OBJ, wallet);
		c.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_list);
		walletFather = (Wallet) getIntent().getSerializableExtra(Wallet.OBJ);
		registerForContextMenu(getListView());

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		findViewById(R.id.createAccount).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				AccountEdit.callMe(AccountList.this, walletFather);
			}
		});

		findViewById(R.id.createReport).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				ReportEdit.callMe(AccountList.this, walletFather);
			}
		});

		reInit(savedInstanceState);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Account account = (Account) l.getAdapter().getItem(position);
		TransactionList.callMe(AccountList.this, account);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.account_menu, menu);
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert_account:
			AccountEdit.callMe(AccountList.this, walletFather);
			return true;
		case R.id.report_list:
			ReportList.callMe(AccountList.this, walletFather);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		new MenuInflater(this).inflate(R.menu.account_context, menu);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		ArrayAdapter<Account> adapter = (ArrayAdapter<Account>) getListAdapter();
		Account account = adapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.edit_account:
			AccountEdit.callMe(AccountList.this, account);
			return true;
		case R.id.delete_account:
			try {
				DatabaseHelper.getHelper(this).deleteAccount(this, account);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			adapter.remove(account);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(Wallet.OBJ, walletFather);
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			fillList();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void fillList() throws SQLException {
		Log.i(TAG, "Show list again");
		Dao<Account, Integer> dao = DatabaseHelper.getHelper(this).getAccountDao();
		QueryBuilder<Account, Integer> qb = dao.queryBuilder();
		qb.where().eq(Account.WALLET_ID, walletFather.get_id());
		List<Account> list = dao.query(qb.prepare());
		ArrayAdapter<Account> arrayAdapter = new AccountAdapter(this, R.layout.account_row, list);
		setListAdapter(arrayAdapter);
	}

	private void reInit(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			walletFather = (Wallet) savedInstanceState.get(Wallet.OBJ);
		}
	}

	// CLASE PRIVADA PARA MOSTRAR LA LISTA
	private class AccountAdapter extends ArrayAdapter<Account> {

		public AccountAdapter(Context context, int textViewResourceId, List<Account> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.account_row, null);
			}
			Account account = getItem(position);
			Currency prefCurrency = null;
			fillText(v, R.id.accountName, account.getName());
			try {
				prefCurrency = DatabaseHelper.getHelper(AccountList.this).getCurrencyDao()
						.queryForId(Integer.valueOf(prefs.getString("currency", "1")));
				fillText(v, R.id.accountAmount, account.getTotal(AccountList.this, prefCurrency).toString());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fillText(v, R.id.accountCurrency, prefCurrency.getName());
			return v;
		}

		private void fillText(View v, int id, String text) {
			TextView textView = (TextView) v.findViewById(id);
			textView.setText(text == null ? "" : text);
		}
	}
}
