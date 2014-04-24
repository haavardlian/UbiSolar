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
	private static final int DATABASE_VERSION = 11;

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
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(EnergyOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL(DeviceModel.SQL_DELETE_ENTRIES);
        db.execSQL(EnergyUsageModel.SQL_DELETE_ENTRIES);
        onCreate(db);
	}
}
