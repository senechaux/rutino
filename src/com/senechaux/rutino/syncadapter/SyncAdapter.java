/*
 * Copyright (C) 2010 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.senechaux.rutino.syncadapter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.auth.AuthenticationException;
import org.json.JSONException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.senechaux.rutino.Constants;
import com.senechaux.rutino.db.entities.Wallet;
import com.senechaux.rutino.platform.WalletManager;
import com.senechaux.rutino.utils.NetworkUtilities;

/**
 * SyncAdapter implementation for syncing sample SyncAdapter contacts to the
 * platform ContactOperations provider.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
	private static final String TAG = "SyncAdapter";

	private final AccountManager mAccountManager;
	private final Context mContext;

	private Date mLastUpdated;

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mContext = context;
		mAccountManager = AccountManager.get(context);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
			SyncResult syncResult) {
		List<Wallet> wallets;

		String authtoken = null;
		Log.d(TAG, "Rutino onPerformSync");
		try {
			// use the account manager to request the credentials
			authtoken = mAccountManager
					.blockingGetAuthToken(account, Constants.AUTHTOKEN_TYPE, true /* notifyAuthFailure */);

			// fetch updates from server
			wallets = NetworkUtilities.fetchWalletUpdates(account, authtoken, mLastUpdated);

			// for (Wallet wallet : wallets) {
			// DatabaseHelper.getHelper(this.mContext).genericCreateOrUpdate(this.mContext, wallet);
			// }

			// update the last synced date.
			mLastUpdated = new Date();
			// update platform wallets.
			Log.d(TAG, "Calling walletManager's sync wallets");
			WalletManager.syncWallets(mContext, account.name, wallets);
		} catch (final AuthenticatorException e) {
			syncResult.stats.numParseExceptions++;
			Log.e(TAG, "AuthenticatorException", e);
		} catch (final OperationCanceledException e) {
			Log.e(TAG, "OperationCanceledExcetpion", e);
		} catch (final IOException e) {
			Log.e(TAG, "IOException", e);
			syncResult.stats.numIoExceptions++;
		} catch (final AuthenticationException e) {
			mAccountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, authtoken);
			syncResult.stats.numAuthExceptions++;
			Log.e(TAG, "AuthenticationException", e);
		} catch (final ParseException e) {
			syncResult.stats.numParseExceptions++;
			Log.e(TAG, "ParseException", e);
		} catch (final JSONException e) {
			syncResult.stats.numParseExceptions++;
			Log.e(TAG, "JSONException", e);
		}
	}
}
