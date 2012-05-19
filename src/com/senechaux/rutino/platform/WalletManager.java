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

package com.senechaux.rutino.platform;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.Wallet;

/**
 * Class for managing contacts sync related mOperations
 */
public class WalletManager {
	/**
	 * Custom IM protocol used when storing status messages.
	 */
	public static final String CUSTOM_IM_PROTOCOL = "RutinoSyncAdapter";
	private static final String TAG = "WalletManager";

	/**
	 * Synchronize raw contacts
	 * 
	 * @param context
	 *            The context of Authenticator Activity
	 * @param account
	 *            The username for the account
	 * @param users
	 *            The list of users
	 */
	public static synchronized void syncWallets(Context context, String account, List<Wallet> wallets) {
		final ContentResolver resolver = context.getContentResolver();
		final BatchOperation batchOperation = new BatchOperation(context, resolver);
		Log.d(TAG, "In SyncWallets");
		for (final Wallet wallet : wallets) {
			// add or update wallet
			Log.d(TAG, "In addWallet");
			addWallet(context, account, wallet, batchOperation);
			// A sync adapter should batch operations on multiple contacts,
			// because it will make a dramatic performance difference.
			if (batchOperation.size() >= 50) {
				batchOperation.execute();
			}
		}
		batchOperation.execute();
	}

	/**
	 * Adds a single wallet
	 * 
	 * @param context
	 *            the Authenticator Activity context
	 * @param accountName
	 *            the account the contact belongs to
	 * @param wallet
	 *            the sample SyncAdapter Wallet object
	 */
	private static void addWallet(Context context, String accountName, Wallet wallet, BatchOperation batchOperation) {
		try {
			DatabaseHelper.getHelper(context).getWalletDao().createOrUpdate(wallet);
		} catch (SQLException e) {
			Log.e(TAG, "SQL error in adding wallet", e);
		}
	}

	/**
	 * Returns the RawWallet id for a Rutino SyncAdapter wallet, or 0 if the
	 * Rutino SyncAdapter wallet isn't found.
	 * 
	 * @param context
	 *            the Authenticator Activity context
	 * @param walletId
	 *            the rutino SyncAdapter user ID to lookup
	 * @return the RawWallet id, or 0 if not found
	 */
	private static int lookupRawWallet(Context context, ContentResolver resolver, int walletId) {
		int id = 0;
		
		// query for all accounts that have that password
		Wallet wallet;
		try {
			wallet = DatabaseHelper.getHelper(context)
					.getWalletDao().queryForId(walletId);
			if(wallet != null){
				id = walletId;
			}
		} catch (SQLException e) {
			//e.printStackTrace();
		}
		return id;
	}

	//
	// /**
	// * Add a list of status messages to the contacts provider.
	// *
	// * @param context the context to use
	// * @param accountName the username of the logged in user
	// * @param statuses the list of statuses to store
	// */
	// public static void insertStatuses(Context context, String username,
	// List<User.Status> list) {
	// final ContentValues values = new ContentValues();
	// final ContentResolver resolver = context.getContentResolver();
	// final BatchOperation batchOperation =
	// new BatchOperation(context, resolver);
	// for (final User.Status status : list) {
	// // Look up the user's sample SyncAdapter data row
	// final long userId = status.getUserId();
	// final long profileId = lookupProfile(resolver, userId);
	//
	// // Insert the activity into the stream
	// if (profileId > 0) {
	// values.put(StatusUpdates.DATA_ID, profileId);
	// values.put(StatusUpdates.STATUS, status.getStatus());
	// values.put(StatusUpdates.PROTOCOL, Im.PROTOCOL_CUSTOM);
	// values.put(StatusUpdates.CUSTOM_PROTOCOL, CUSTOM_IM_PROTOCOL);
	// values.put(StatusUpdates.IM_ACCOUNT, username);
	// values.put(StatusUpdates.IM_HANDLE, status.getUserId());
	// values.put(StatusUpdates.STATUS_RES_PACKAGE, context
	// .getPackageName());
	// values.put(StatusUpdates.STATUS_ICON, R.drawable.icon);
	// values.put(StatusUpdates.STATUS_LABEL, R.string.label);
	//
	// batchOperation
	// .add(ContactOperations.newInsertCpo(
	// StatusUpdates.CONTENT_URI, true).withValues(values)
	// .build());
	// // A sync adapter should batch operations on multiple contacts,
	// // because it will make a dramatic performance difference.
	// if (batchOperation.size() >= 50) {
	// batchOperation.execute();
	// }
	// }
	// }
	// batchOperation.execute();
	// }
	//

	//
	// /**
	// * Updates a single contact to the platform contacts provider.
	// *
	// * @param context the Authenticator Activity context
	// * @param resolver the ContentResolver to use
	// * @param accountName the account the contact belongs to
	// * @param user the sample SyncAdapter contact object.
	// * @param rawContactId the unique Id for this rawContact in contacts
	// * provider
	// */
	// private static void updateContact(Context context,
	// ContentResolver resolver, String accountName, User user,
	// long rawContactId, BatchOperation batchOperation) {
	// Uri uri;
	// String cellPhone = null;
	// String otherPhone = null;
	// String email = null;
	//
	// final Cursor c =
	// resolver.query(Data.CONTENT_URI, DataQuery.PROJECTION,
	// DataQuery.SELECTION,
	// new String[] {String.valueOf(rawContactId)}, null);
	// final ContactOperations contactOp =
	// ContactOperations.updateExistingContact(context, rawContactId,
	// batchOperation);
	//
	// try {
	// while (c.moveToNext()) {
	// final long id = c.getLong(DataQuery.COLUMN_ID);
	// final String mimeType = c.getString(DataQuery.COLUMN_MIMETYPE);
	// uri = ContentUris.withAppendedId(Data.CONTENT_URI, id);
	//
	// if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
	// final String lastName =
	// c.getString(DataQuery.COLUMN_FAMILY_NAME);
	// final String firstName =
	// c.getString(DataQuery.COLUMN_GIVEN_NAME);
	// contactOp.updateName(uri, firstName, lastName, user
	// .getFirstName(), user.getLastName());
	// }
	//
	// else if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
	// final int type = c.getInt(DataQuery.COLUMN_PHONE_TYPE);
	//
	// if (type == Phone.TYPE_MOBILE) {
	// cellPhone = c.getString(DataQuery.COLUMN_PHONE_NUMBER);
	// contactOp.updatePhone(cellPhone, user.getCellPhone(),
	// uri);
	// } else if (type == Phone.TYPE_OTHER) {
	// otherPhone = c.getString(DataQuery.COLUMN_PHONE_NUMBER);
	// contactOp.updatePhone(otherPhone, user.getHomePhone(),
	// uri);
	// }
	// }
	//
	// else if (Data.MIMETYPE.equals(Email.CONTENT_ITEM_TYPE)) {
	// email = c.getString(DataQuery.COLUMN_EMAIL_ADDRESS);
	// contactOp.updateEmail(user.getEmail(), email, uri);
	//
	// }
	// } // while
	// } finally {
	// c.close();
	// }
	//
	// // Add the cell phone, if present and not updated above
	// if (cellPhone == null) {
	// contactOp.addPhone(user.getCellPhone(), Phone.TYPE_MOBILE);
	// }
	//
	// // Add the other phone, if present and not updated above
	// if (otherPhone == null) {
	// contactOp.addPhone(user.getHomePhone(), Phone.TYPE_OTHER);
	// }
	//
	// // Add the email address, if present and not updated above
	// if (email == null) {
	// contactOp.addEmail(user.getEmail());
	// }
	//
	// }
	//
	// /**
	// * Deletes a contact from the platform contacts provider.
	// *
	// * @param context the Authenticator Activity context
	// * @param rawContactId the unique Id for this rawContact in contacts
	// * provider
	// */
	// private static void deleteContact(Context context, long rawContactId,
	// BatchOperation batchOperation) {
	// batchOperation.add(ContactOperations.newDeleteCpo(
	// ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId),
	// true).build());
	// }

	//
	// /**
	// * Returns the Data id for a sample SyncAdapter contact's profile row, or
	// 0
	// * if the sample SyncAdapter user isn't found.
	// *
	// * @param resolver a content resolver
	// * @param userId the sample SyncAdapter user ID to lookup
	// * @return the profile Data row id, or 0 if not found
	// */
	// private static long lookupProfile(ContentResolver resolver, long userId)
	// {
	// long profileId = 0;
	// final Cursor c =
	// resolver.query(Data.CONTENT_URI, ProfileQuery.PROJECTION,
	// ProfileQuery.SELECTION, new String[] {String.valueOf(userId)},
	// null);
	// try {
	// if (c != null && c.moveToFirst()) {
	// profileId = c.getLong(ProfileQuery.COLUMN_ID);
	// }
	// } finally {
	// if (c != null) {
	// c.close();
	// }
	// }
	// return profileId;
	// }
	//
	// /**
	// * Constants for a query to find a contact given a sample SyncAdapter user
	// * ID.
	// */
	// private interface ProfileQuery {
	// public final static String[] PROJECTION = new String[] {Data._ID};
	//
	// public final static int COLUMN_ID = 0;
	//
	// public static final String SELECTION =
	// Data.MIMETYPE + "='" + SampleSyncAdapterColumns.MIME_PROFILE
	// + "' AND " + SampleSyncAdapterColumns.DATA_PID + "=?";
	// }
	// /**
	// * Constants for a query to find a contact given a sample SyncAdapter user
	// * ID.
	// */
	// private interface UserIdQuery {
	// public final static String[] PROJECTION =
	// new String[] {RawContacts._ID};
	//
	// public final static int COLUMN_ID = 0;
	//
	// public static final String SELECTION =
	// RawContacts.ACCOUNT_TYPE + "='" + Constants.ACCOUNT_TYPE + "' AND "
	// + RawContacts.SOURCE_ID + "=?";
	// }
	//
	// /**
	// * Constants for a query to get contact data for a given rawContactId
	// */
	// private interface DataQuery {
	// public static final String[] PROJECTION =
	// new String[] {Data._ID, Data.MIMETYPE, Data.DATA1, Data.DATA2,
	// Data.DATA3,};
	//
	// public static final int COLUMN_ID = 0;
	// public static final int COLUMN_MIMETYPE = 1;
	// public static final int COLUMN_DATA1 = 2;
	// public static final int COLUMN_DATA2 = 3;
	// public static final int COLUMN_DATA3 = 4;
	// public static final int COLUMN_PHONE_NUMBER = COLUMN_DATA1;
	// public static final int COLUMN_PHONE_TYPE = COLUMN_DATA2;
	// public static final int COLUMN_EMAIL_ADDRESS = COLUMN_DATA1;
	// public static final int COLUMN_EMAIL_TYPE = COLUMN_DATA2;
	// public static final int COLUMN_GIVEN_NAME = COLUMN_DATA2;
	// public static final int COLUMN_FAMILY_NAME = COLUMN_DATA3;
	//
	// public static final String SELECTION = Data.RAW_CONTACT_ID + "=?";
	// }
}

