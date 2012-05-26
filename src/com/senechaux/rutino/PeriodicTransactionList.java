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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.AccountEntity;
import com.senechaux.rutino.db.entities.Currency;
import com.senechaux.rutino.db.entities.PeriodicTransaction;

@SuppressWarnings("unchecked")
public class PeriodicTransactionList extends ListActivity {
	private static final String TAG = "PeriodicTransactionList";
	private AccountEntity accountFather;
	private Button createPeriodicTransaction;

	public static void callMe(Context c, AccountEntity accountEntity) {
		Intent intent = new Intent(c, PeriodicTransactionList.class);
		intent.putExtra(AccountEntity.OBJ, accountEntity);
		c.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.periodic_transaction_list);
		createPeriodicTransaction = (Button) findViewById(R.id.createPeriodicTransaction);

		accountFather = (AccountEntity) getIntent().getSerializableExtra(AccountEntity.OBJ);
		registerForContextMenu(getListView());

		createPeriodicTransaction.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				TransactionEdit.callMe(PeriodicTransactionList.this, accountFather);
			}
		});

		reInit(savedInstanceState);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		PeriodicTransaction perTransaction = (PeriodicTransaction) l.getAdapter().getItem(position);
		TransactionEdit.callMe(PeriodicTransactionList.this, perTransaction);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.periodic_transaction_menu, menu);
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert_periodic_transaction:
			TransactionEdit.callMe(PeriodicTransactionList.this, accountFather);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		new MenuInflater(this).inflate(R.menu.periodic_transaction_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		ArrayAdapter<PeriodicTransaction> adapter = (ArrayAdapter<PeriodicTransaction>) getListAdapter();
		PeriodicTransaction perTransaction = adapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.edit_periodic_transaction:
			TransactionEdit.callMe(PeriodicTransactionList.this, perTransaction);
			return true;
		case R.id.delete_periodic_transaction:
			try {
				DatabaseHelper.getHelper(this).deletePeriodicTransaction(this, perTransaction);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			adapter.remove(perTransaction);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(AccountEntity.OBJ, accountFather);
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
		Dao<PeriodicTransaction, Integer> dao = (Dao<PeriodicTransaction, Integer>) DatabaseHelper.getHelper(this)
				.getMyDao(PeriodicTransaction.class);
		QueryBuilder<PeriodicTransaction, Integer> qb = dao.queryBuilder();
		qb.where().eq(PeriodicTransaction.ACCOUNTENTITY_ID, accountFather.get_id());
		// false: más reciente primero
		qb.orderBy(PeriodicTransaction.DATE, false);
		List<PeriodicTransaction> list = dao.query(qb.prepare());
		ArrayAdapter<PeriodicTransaction> arrayAdapter = new PeriodicTransactionAdapter(this, R.layout.transaction_row,
				list);
		setListAdapter(arrayAdapter);
	}

	private void reInit(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			accountFather = (AccountEntity) savedInstanceState.get(AccountEntity.OBJ);
		}
	}

	// CLASE PRIVADA PARA MOSTRAR LA LISTA
	private class PeriodicTransactionAdapter extends ArrayAdapter<PeriodicTransaction> {

		public PeriodicTransactionAdapter(Context context, int textViewResourceId, List<PeriodicTransaction> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.periodic_transaction_row, null);
			}
			PeriodicTransaction transaction = getItem(position);
			fillText(v, R.id.periodicTransactionName, transaction.getName());
			fillText(v, R.id.periodicTransactionAmount, transaction.getAmount().toString());
			try {
				fillText(
						v,
						R.id.periodicTransactionCurrency,
						((Dao<Currency, Integer>) DatabaseHelper.getHelper(PeriodicTransactionList.this).getMyDao(
								Currency.class)).queryForId(transaction.getCurrency().get_id()).getName());
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
