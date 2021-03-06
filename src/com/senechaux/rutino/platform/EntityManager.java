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

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.senechaux.rutino.db.DatabaseHelper;
import com.senechaux.rutino.db.entities.BaseEntity;

/**
 * Class for managing wallets sync related mOperations
 */
public class EntityManager {
	/**
	 * Custom IM protocol used when storing status messages.
	 */
	public static final String CUSTOM_IM_PROTOCOL = "RutinoSyncAdapter";
	private static final String TAG = "EntityManager";

	/**
	 * Synchronize raw wallets
	 * 
	 * @param context
	 *            The context of Authenticator Activity
	 * @param entities
	 *            The list of entities
	 */
	public static synchronized void syncEntities(Context context, List<BaseEntity> entities) {
		Log.d(TAG, "In EntityManager");
		for (final BaseEntity entity : entities) {
			// add or update wallet
			try {
				DatabaseHelper.getHelper(context).genericCreateOrUpdate(context, entity);
			} catch (SQLException e) {
				Log.e(TAG, "SQL error in adding " + entity.getClass().getName(), e);
			}
		}
	}

}
