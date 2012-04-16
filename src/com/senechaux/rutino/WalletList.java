package com.senechaux.rutino;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.WalletDbAdapter;

public class WalletList extends ListActivity {
    private WalletDbAdapter mDbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallets_list);
        mDbHelper = new WalletDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }

    private void fillData() {
        Cursor walletsCursor = mDbHelper.readAllWallets();
        startManagingCursor(walletsCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{DatabaseHelper.WALLETS_TITLE};

        // and an array of the fields we want to bind those fields to (in this case just walletTitle)
        int[] to = new int[]{R.id.walletTitle};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter wallets = 
            new SimpleCursorAdapter(this, R.layout.wallets_row, walletsCursor, from, to);
        setListAdapter(wallets);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	new MenuInflater(this).inflate(R.menu.wallet_menu, menu);
    	return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case R.id.insert_wallet:
                createWallet();
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
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
        case R.id.delete_wallet:
            mDbHelper.deleteWallet(info.id);
            fillData();
            return true;
        case R.id.edit_wallet:
            Intent i = new Intent(this, WalletEdit.class);
            i.putExtra(DatabaseHelper.WALLETS_ID, info.id);
            startActivityForResult(i, Constants.ACTIVITY_EDIT_WALLET);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createWallet() {
        Intent i = new Intent(this, WalletEdit.class);
        startActivityForResult(i, Constants.ACTIVITY_CREATE_WALLET);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, WalletEdit.class);
        i.putExtra(DatabaseHelper.WALLETS_ID, id);
        startActivityForResult(i, Constants.ACTIVITY_EDIT_WALLET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}
