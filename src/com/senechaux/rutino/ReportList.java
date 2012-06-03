package com.senechaux.rutino;

import java.sql.SQLException;
import java.text.DateFormat;
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
import com.senechaux.rutino.db.entities.Report;
import com.senechaux.rutino.db.entities.Wallet;

@SuppressWarnings("unchecked")
public class ReportList extends ListActivity {
	private static final String TAG = "ReportList";
	private Wallet walletFather;

	public static void callMe(Context c, Wallet wallet) {
		Intent intent = new Intent(c, ReportList.class);
		intent.putExtra(Wallet.OBJ, wallet);
		c.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_list);
		walletFather = (Wallet) getIntent().getSerializableExtra(Wallet.OBJ);
		registerForContextMenu(getListView());

		findViewById(R.id.createReport).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				ReportEdit.callMe(ReportList.this, walletFather);
			}
		});

		reInit(savedInstanceState);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Report report = (Report) l.getAdapter().getItem(position);
		ReportView.callMe(ReportList.this, report);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.report_menu, menu);
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert_report:
			ReportEdit.callMe(ReportList.this, walletFather);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		new MenuInflater(this).inflate(R.menu.report_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		ArrayAdapter<Report> adapter = (ArrayAdapter<Report>) getListAdapter();
		Report report = adapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.edit_report:
			ReportEdit.callMe(ReportList.this, report);
			return true;
		case R.id.delete_report:
			try {
				((Dao<Report, Integer>) DatabaseHelper.getHelper(this).getMyDao(Report.class)).delete(report);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			adapter.remove(report);
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
//		Dao<Report, Integer> dao = (Dao<Report, Integer>) DatabaseHelper.getHelper(this).getMyDao(Report.class);
//		QueryBuilder<Report, Integer> qb = dao.queryBuilder();
//		qb.where().eq(Report.WALLET_ID, walletFather.get_id());
//		List<Report> list = dao.query(qb.prepare());
		List<Report> list = Report.dameListadoReportes(this, walletFather);
		ArrayAdapter<Report> arrayAdapter = new ReportAdapter(this, R.layout.report_row, list);
		setListAdapter(arrayAdapter);
	}

	private void reInit(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			walletFather = (Wallet) savedInstanceState.get(Wallet.OBJ);
		}
	}

	// CLASE PRIVADA PARA MOSTRAR LA LISTA
	private class ReportAdapter extends ArrayAdapter<Report> {
		DateFormat df = DateFormat.getDateInstance();

		public ReportAdapter(Context context, int textViewResourceId, List<Report> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.report_row, null);
			}
			Report report = getItem(position);
			fillText(v, R.id.reportName, report.getName());
			fillText(v, R.id.reportDateFrom, df.format(report.getDateFrom()));
			fillText(v, R.id.reportDateTo, df.format(report.getDateTo()));
			return v;
		}

		private void fillText(View v, int id, String text) {
			TextView textView = (TextView) v.findViewById(id);
			textView.setText(text == null ? "" : text);
		}
	}
}
