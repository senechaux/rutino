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

package com.senechaux.rutino.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.senechaux.rutino.Constants;
import com.senechaux.rutino.R;
import com.senechaux.rutino.utils.NetworkUtilities;

/**
 * This class is an implementation of AbstractAccountAuthenticator for authenticating accounts in the
 * com.example.android.samplesync domain.
 */
class Authenticator extends AbstractAccountAuthenticator {
	// Authentication Service context
	private final Context mContext;

	public Authenticator(Context context) {
		super(context);
		mContext = context;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options) {
		final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
		intent.putExtra(AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
		if (options != null && options.containsKey(AccountManager.KEY_PASSWORD)) {
			final String password = options.getString(AccountManager.KEY_PASSWORD);
			final boolean verified = onlineConfirmPassword(account.name, password);
			final String token = onlineConfirmPasswordAndGetToken(account.name, password);
			final Bundle result = new Bundle();
			result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, verified);
			result.putString(AccountManager.KEY_AUTHTOKEN, token);
			return result;
		}
		// Launch AuthenticatorActivity to confirm credentials
		final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
		intent.putExtra(AuthenticatorActivity.PARAM_USERNAME, account.name);
		intent.putExtra(AuthenticatorActivity.PARAM_CONFIRMCREDENTIALS, true);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType,
			Bundle loginOptions) {
		if (!authTokenType.equals(Constants.AUTHTOKEN_TYPE)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
			return result;
		}
		final AccountManager am = AccountManager.get(mContext);
		final String password = am.getPassword(account);
		final Bundle options = new Bundle();
		options.putString(AccountManager.KEY_PASSWORD, password);
		Bundle authToken = confirmCredentials(response, account, options);
		if (password != null) {
			final boolean verified = onlineConfirmPassword(account.name, password);
			if (verified) {
				final Bundle result = new Bundle();
				result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
				result.putString(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
				result.putString(AccountManager.KEY_AUTHTOKEN, authToken.getString(AccountManager.KEY_AUTHTOKEN));
				return result;
			}
		}
		// the password was missing or incorrect, return an Intent to an
		// Activity that will prompt the user for the password.
		final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
		intent.putExtra(AuthenticatorActivity.PARAM_USERNAME, account.name);
		intent.putExtra(AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAuthTokenLabel(String authTokenType) {
		if (authTokenType.equals(Constants.AUTHTOKEN_TYPE)) {
			return mContext.getString(R.string.app_name);
		}
		return null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) {
		final Bundle result = new Bundle();
		result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
		return result;
	}

	/**
	 * Validates user's password on the server
	 */
	private boolean onlineConfirmPassword(String username, String password) {
		return NetworkUtilities.authenticate(username, password, null/* Handler */, null/* Context */);
	}

	private String onlineConfirmPasswordAndGetToken(String username, String password) {
		return NetworkUtilities.authenticateGetToken(username, password, null/* Handler */, null/* Context */);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType,
			Bundle loginOptions) {
		final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
		intent.putExtra(AuthenticatorActivity.PARAM_USERNAME, account.name);
		intent.putExtra(AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
		intent.putExtra(AuthenticatorActivity.PARAM_CONFIRMCREDENTIALS, false);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

}
