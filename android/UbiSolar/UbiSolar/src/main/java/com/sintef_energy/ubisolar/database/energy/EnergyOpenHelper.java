package com.sintef_energy.ubisolar.database.energy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sintef_energy.ubisolar.utils.Log;

/**
 * Created by perok on 2/11/14.
 */
public class EnergyOpenHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "energy.db";
	private static final int DATABASE_VERSION = 1;

	public EnergyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}

	public EnergyOpenHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(EnergyModel.SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(EnergyOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL(EnergyModel.SQL_DELETE_ENTRIES);
        onCreate(db);
	}
}
