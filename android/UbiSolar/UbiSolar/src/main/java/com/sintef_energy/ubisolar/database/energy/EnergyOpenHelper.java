/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.sintef_energy.ubisolar.database.energy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by perok on 2/11/14.
 */
public class EnergyOpenHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "energy.db";
	private static final int DATABASE_VERSION = 15;

	public EnergyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}

	public EnergyOpenHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
        database.execSQL("PRAGMA foreign_keys=ON;"); //Activate foreign keys in sqlite
		database.execSQL(DeviceModel.SQL_CREATE_ENTRIES);
        database.execSQL(EnergyUsageModel.SQL_CREATE_ENTRIES);
        database.execSQL(ResidenceModel.SQL_CREATE_ENTRIES);
        database.execSQL(UserModel.SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(EnergyOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL(DeviceModel.SQL_DELETE_ENTRIES);
        db.execSQL(EnergyUsageModel.SQL_DELETE_ENTRIES);
        db.execSQL(ResidenceModel.SQL_DELETE_ENTRIES);
        db.execSQL(UserModel.SQL_DELETE_ENTRIES);
        onCreate(db);
	}
}
