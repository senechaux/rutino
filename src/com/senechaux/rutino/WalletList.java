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
import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.Wallet;

public class WalletList extends ListActivity {

	// private final DateFormat df = new SimpleDateFormat("M/dd/yy HH:mm");

	public static void callMe(Context c) {
		c.startActivity(new Intent(c, WalletList.class));
	}

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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Wallet wallet = (Wallet) l.getAdapter().getItem(position);
		AccountList.callMe(WalletList.this, wallet);
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
		case R.id.preferences:
			Preferences.callMe(WalletList.this);
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
	@SuppressWarnings("unchecked")
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		ArrayAdapter<Wallet> adapter = (ArrayAdapter<Wallet>) getListAdapter();
		Wallet wallet = adapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.edit_wallet:
			WalletEdit.callMe(WalletList.this, wallet);
			return true;
		case R.id.delete_wallet:
			try {
//				getHelper().getWalletDao().deleteById(wallet.get_id());
				DatabaseHelper.getInstance(this).getWalletDao().deleteById(wallet.get_id());
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			adapter.remove(wallet);
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

	private void fillList() throws SQLException {
		Log.i(WalletList.class.getName(), "Show list again");
//		Dao<Wallet, Integer> dao = getHelper().getWalletDao();
		Dao<Wallet, Integer> dao = DatabaseHelper.getInstance(this).getWalletDao();
		List<Wallet> list = dao.queryForAll();
		ArrayAdapter<Wallet> arrayAdapter = new WalletAdapter(this,
				R.layout.wallet_row, list);
		setListAdapter(arrayAdapter);
	}

	// CLASE PRIVADA PARA MOSTRAR LA LISTA
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
