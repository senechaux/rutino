package com.senechaux.rutino;

import java.sql.SQLException;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.senechaux.rutino.db.entities.Transaction;

public class TransactionList extends ListActivity {
	private Account accountFather;

	public static void callMe(Context c, Account account) {
		Intent intent = new Intent(c, TransactionList.class);
		intent.putExtra(Account.OBJ, account);
		c.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_list);
		accountFather = (Account) getIntent().getSerializableExtra(Account.OBJ);
		registerForContextMenu(getListView());

		findViewById(R.id.createTransaction).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				TransactionEdit.callMe(TransactionList.this, accountFather);
			}
		});

		reInit(savedInstanceState);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Transaction transaction = (Transaction) l.getAdapter().getItem(position);
		TransactionEdit.callMe(TransactionList.this, transaction);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.transaction_menu, menu);
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert_transaction:
			TransactionEdit.callMe(TransactionList.this, accountFather);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		new MenuInflater(this).inflate(R.menu.transaction_context, menu);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		ArrayAdapter<Transaction> adapter = (ArrayAdapter<Transaction>) getListAdapter();
		Transaction transaction = adapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.edit_transaction:
			TransactionEdit.callMe(TransactionList.this, transaction);
			return true;
		case R.id.delete_transaction:
			try {
				DatabaseHelper.getHelper(this).getTransactionDao().deleteById(transaction.get_id());
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			adapter.remove(transaction);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(Account.OBJ, accountFather);
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
		Log.i(TransactionList.class.getName(), "Show list again");
		Dao<Transaction, Integer> dao = DatabaseHelper.getHelper(this).getTransactionDao();
		QueryBuilder<Transaction, Integer> qb = dao.queryBuilder();
		qb.where().eq(Transaction.ACCOUNT_ID, accountFather.get_id());
		// false: más reciente primero
		qb.orderBy(Transaction.DATE, false);
		List<Transaction> list = dao.query(qb.prepare());
		ArrayAdapter<Transaction> arrayAdapter = new TransactionAdapter(this, R.layout.transaction_row, list);
		setListAdapter(arrayAdapter);
	}

	private void reInit(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			accountFather = (Account) savedInstanceState.get(Account.OBJ);
		}
	}

	// CLASE PRIVADA PARA MOSTRAR LA LISTA
	private class TransactionAdapter extends ArrayAdapter<Transaction> {

		public TransactionAdapter(Context context, int textViewResourceId, List<Transaction> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.transaction_row, null);
			}
			Transaction transaction = getItem(position);
			fillText(v, R.id.transactionName, transaction.getName());
			fillText(v, R.id.transactionAmount, transaction.getAmount().toString());
			try {
				fillText(v, R.id.transactionCurrency, DatabaseHelper.getHelper(TransactionList.this).getCurrencyDao()
						.queryForId(transaction.getCurrency().get_id()).getName());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return v;
		}

		private void fillText(View v, int id, String text) {
			TextView textView = (TextView) v.findViewById(id);
			textView.setText(text == null ? "" : text);
		}
	}
}
