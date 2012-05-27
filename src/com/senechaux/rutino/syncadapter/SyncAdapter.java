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
import com.senechaux.rutino.db.entities.AccountEntity;
import com.senechaux.rutino.db.entities.AccountType;
import com.senechaux.rutino.db.entities.BaseEntity;
import com.senechaux.rutino.db.entities.Currency;
import com.senechaux.rutino.db.entities.PeriodicTransaction;
import com.senechaux.rutino.db.entities.Report;
import com.senechaux.rutino.db.entities.Transaction;
import com.senechaux.rutino.db.entities.Wallet;
import com.senechaux.rutino.platform.EntityManager;
import com.senechaux.rutino.utils.NetworkUtilities;

/**
 * SyncAdapter implementation for syncing sample SyncAdapter contacts to the platform ContactOperations provider.
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
		List<BaseEntity> entities;

		String authtoken = null;
		Log.d(TAG, "Rutino onPerformSync");
		try {
			// use the account manager to request the credentials
			authtoken = mAccountManager
					.blockingGetAuthToken(account, Constants.AUTHTOKEN_TYPE, true /* notifyAuthFailure */);

			// fetch updates from server
			// wallets = NetworkUtilities.fetchWalletUpdates(account, authtoken,
			// mLastUpdated);

			// SINCRONIZAMOS Wallet
			Log.d(TAG, "Sync Wallet");
			entities = NetworkUtilities.fetchEntityUpdates(this.mContext, account, authtoken, mLastUpdated,
					Constants.GET_WALLET_LIST, Wallet.class);
			EntityManager.syncEntities(mContext, entities);

			// SINCRONIZAMOS Report
			Log.d(TAG, "Sync Report");
			entities = NetworkUtilities.fetchEntityUpdates(this.mContext, account, authtoken, mLastUpdated,
					Constants.GET_REPORT_LIST, Report.class);
			EntityManager.syncEntities(mContext, entities);

			// SINCRONIZAMOS AccountType
			Log.d(TAG, "Sync AccountType");
			entities = NetworkUtilities.fetchEntityUpdates(this.mContext, account, authtoken, mLastUpdated,
					Constants.GET_ACCOUNTTYPE_LIST, AccountType.class);
			EntityManager.syncEntities(mContext, entities);

			// SINCRONIZAMOS Currency
			Log.d(TAG, "Calling EntityManager sync Currency");
			entities = NetworkUtilities.fetchEntityUpdates(this.mContext, account, authtoken, mLastUpdated,
					Constants.GET_CURRENCY_LIST, Currency.class);
			EntityManager.syncEntities(mContext, entities);

			// SINCRONIZAMOS AccountEntity
			Log.d(TAG, "Sync AccountEntity");
			entities = NetworkUtilities.fetchEntityUpdates(this.mContext, account, authtoken, mLastUpdated,
					Constants.GET_ACCOUNT_LIST, AccountEntity.class);
			EntityManager.syncEntities(mContext, entities);

			// SINCRONIZAMOS Transaction
			Log.d(TAG, "Sync Transaction");
			entities = NetworkUtilities.fetchEntityUpdates(this.mContext, account, authtoken, mLastUpdated,
					Constants.GET_TRANSACTION_LIST, Transaction.class);
			EntityManager.syncEntities(mContext, entities);

			// SINCRONIZAMOS PeriodicTransaction
			Log.d(TAG, "Sync PeriodicTransaction");
			entities = NetworkUtilities.fetchEntityUpdates(this.mContext, account, authtoken, mLastUpdated,
					Constants.GET_PERIODIC_TRANSACTION_LIST, PeriodicTransaction.class);
			EntityManager.syncEntities(mContext, entities);

			// transactions = NetworkUtilities.fetchEntityUpdates(this.mContext, account, authtoken, mLastUpdated);
			// periodicTransactions = NetworkUtilities.fetchEntityUpdates(this.mContext, account, authtoken,
			// mLastUpdated);
			// reports = NetworkUtilities.fetchEntityUpdates(this.mContext, account, authtoken, mLastUpdated);

			// update the last synced date.
			mLastUpdated = new Date();
			// update platform wallets.
			// AccountManager.syncAccounts(mContext, accounts);
			// Log.d(TAG, "Calling TransactionManager's sync transactions");
			// TransactionManager.syncTransactions(mContext, account.name, transactions);
			// Log.d(TAG, "Calling PeriodicTransactionManager's sync periodic transactions");
			// PeriodicTransactionManager.syncPeriodicTransactions(mContext, account.name, periodicTransactions);
			// Log.d(TAG, "Calling ReportManager's sync reports");
			// ReportManager.syncReports(mContext, account.name, reports);

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
