package com.senechaux.rutino;

import java.sql.SQLException;
import java.util.List;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.Wallet;

public class WalletList extends OrmLiteBaseListActivity<DatabaseHelper> {

	// private final DateFormat df = new SimpleDateFormat("M/dd/yy HH:mm");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wallet_list);
        registerForContextMenu(getListView());

		findViewById(R.id.createWallet).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						WalletEdit.callMe(WalletList.this);
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.wallet_menu, menu);
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert_wallet:
			WalletEdit.callMe(WalletList.this);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		new MenuInflater(this).inflate(R.menu.wallet_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Wallet wallet = (Wallet) item;
		switch (item.getItemId()) {
		case R.id.edit_wallet:
			WalletEdit.callMe(WalletList.this, wallet.getId());
			return true;
		case R.id.delete_wallet:
			try {
				getHelper().getWalletDao().deleteById(wallet.getId());
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			return true;
		}
		return super.onContextItemSelected(item);
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

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Wallet wallet = (Wallet) l.getAdapter().getItem(position);
		// CounterScreen.callMe(WalletList.this, wallet.getId());
		WalletEdit.callMe(WalletList.this, wallet.getId());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		try {
			fillList();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void fillList() throws SQLException {
		Log.i(WalletList.class.getName(), "Show list again");
		Dao<Wallet, Integer> dao = getHelper().getWalletDao();
		QueryBuilder<Wallet, Integer> builder = dao.queryBuilder();
		// builder.orderBy(Wallet.DATE_FIELD_NAME, false).limit(30L);
		List<Wallet> list = dao.query(builder.prepare());
		ArrayAdapter<Wallet> arrayAdapter = new WalletAdapter(this,
				R.layout.wallet_row, list);
		setListAdapter(arrayAdapter);
	}

	private class WalletAdapter extends ArrayAdapter<Wallet> {

		public WalletAdapter(Context context, int textViewResourceId,
				List<Wallet> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.wallet_row, null);
			}
			Wallet wallet = getItem(position);
			fillText(v, R.id.walletName, wallet.getName());
			return v;
		}

		private void fillText(View v, int id, String text) {
			TextView textView = (TextView) v.findViewById(id);
			textView.setText(text == null ? "" : text);
		}
	}
}
