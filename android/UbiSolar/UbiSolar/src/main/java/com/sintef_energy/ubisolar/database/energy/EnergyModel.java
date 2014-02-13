package com.sintef_energy.ubisolar.database.energy;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

/**
 * Created by perok on 2/11/14.
 */
public class EnergyModel implements Parcelable{
    private static final String TAG = EnergyModel.class.getName();

    /* Column definitions*/
    public static interface EnergyEntry extends BaseColumns {
        public static final String TABLE_NAME = "Energy";
        public static final String COLUMN_X = "x";
        //TODO
    }

    public static final String[] projection = new String[]{
            EnergyEntry._ID,
            EnergyEntry.COLUMN_X
    };

    /* SQL Statements*/
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EnergyEntry.TABLE_NAME + " (" +
                    EnergyEntry._ID + " INTEGER PRIMARY KEY," +
                    EnergyEntry.COLUMN_X + INTEGER_TYPE + COMMA_SEP +
                    " )";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + EnergyEntry.TABLE_NAME;

    /* POJO */
    private long id;
    private int _id = 0;
    private String x;
    private int _x = 1;


    /**
     * Create CalendarEventModel with default values. All relation ID's are '-1'
     */
    public EnergyModel() {
        setId(-1);
    }

    /* Parcable */
    public EnergyModel(Parcel in) {
        readFromParcel(in);
    }

    public static final Parcelable.Creator<EnergyModel> CREATOR = new Parcelable.Creator<EnergyModel>() {

        public EnergyModel createFromParcel(Parcel in) {
            return new EnergyModel(in);
        }

        public EnergyModel[] newArray(int size) {
            return new EnergyModel[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
    }

    private void readFromParcel(Parcel in) {
        setId(in.readLong());
    }

    /**
     * Get all the values in ContentValues class.
     * @return
     */
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(EnergyEntry._ID, id);
        return values;
    }


    /**
     * Create CalendarEventModel from cursor
     * @param cursor
     */
    public EnergyModel(Cursor cursor) {
        setId(cursor.getLong(_id));
    }

    /* Getters and setters */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
