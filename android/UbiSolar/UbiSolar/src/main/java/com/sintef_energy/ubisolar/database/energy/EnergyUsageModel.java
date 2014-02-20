package com.sintef_energy.ubisolar.database.energy;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import java.util.Comparator;

/**
 * Created by perok on 2/11/14.
 */
public class EnergyUsageModel implements Parcelable, Comparable<EnergyUsageModel>{
    private static final String TAG = EnergyUsageModel.class.getName();

    @Override
    public int compareTo(EnergyUsageModel energyUsageModel) {
        long one = getDateStart();
        long two = energyUsageModel.getDateStart();

        if(one == two)
            return 0;
        else if(one > two)
            return 1;
        else
            return -1;
    }

    /* Column definitions*/
    public static interface EnergyUsageEntry extends BaseColumns {
        public static final String TABLE_NAME = "energy";
        public static final String COLUMN_DEVICE_ID = "fkdeviceid";
        public static final String COLUMN_DATESTART = "datestart";
        public static final String COLUMN_DATEEND = "dateend";
        public static final String COLUMN_POWER = "power";
    }

    public static final String[] projection = new String[]{
            EnergyUsageEntry._ID,
            EnergyUsageEntry.COLUMN_DEVICE_ID,
            EnergyUsageEntry.COLUMN_DATESTART,
            EnergyUsageEntry.COLUMN_DATEEND,
            EnergyUsageEntry.COLUMN_POWER

    };

    /* SQL Statements*/
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EnergyUsageEntry.TABLE_NAME + " (" +
                    EnergyUsageEntry._ID + " INTEGER PRIMARY KEY," +
                    EnergyUsageEntry.COLUMN_DEVICE_ID + INTEGER_TYPE + COMMA_SEP +
                    EnergyUsageEntry.COLUMN_DATESTART + INTEGER_TYPE + COMMA_SEP +
                    EnergyUsageEntry.COLUMN_DATEEND + INTEGER_TYPE + COMMA_SEP +
                    EnergyUsageEntry.COLUMN_POWER + REAL_TYPE + COMMA_SEP +
                    "FOREIGN KEY(" + EnergyUsageEntry.COLUMN_DEVICE_ID +
                        ") REFERENCES " + DeviceModel.DeviceEntry.TABLE_NAME +
                            "(" + DeviceModel.DeviceEntry._ID + ")" +
                    " )";
    //FOREIGN KEY(foreign_key_name) REFERENCES one_table_name(primary_key_name)

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + EnergyUsageEntry.TABLE_NAME;

    /* POJO */
    private long id;
    private int _id = 0;
    private long deviceId;
    private int _deviceId = 1;
    private long dateStart;
    private int _dateStart = 2;
    private long dateEnd;
    private int _dateEnd = 3;
    private float power;
    private int _power = 4;


    /**
     * Create CalendarEventModel with default values. All relation ID's are '-1'
     */
    public EnergyUsageModel() {
        setId(-1);
        setDeviceId(-1);
        setDateStart(-1);
        setDateEnd(-1);
        setPower(-1);
    }

    /* Parcable */
    public EnergyUsageModel(Parcel in) {
        readFromParcel(in);
    }

    public static final Creator<EnergyUsageModel> CREATOR = new Creator<EnergyUsageModel>() {

        public EnergyUsageModel createFromParcel(Parcel in) {
            return new EnergyUsageModel(in);
        }

        public EnergyUsageModel[] newArray(int size) {
            return new EnergyUsageModel[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeLong(deviceId);
        out.writeLong(dateStart);
        out.writeLong(dateEnd);
        out.writeFloat(power);
    }

    private void readFromParcel(Parcel in) {
        setId(in.readLong());
        setDeviceId(in.readLong());
        setDateStart(in.readLong());
        setDateEnd(in.readLong());
        setPower(in.readFloat());
    }

    /**
     * Get all the values in ContentValues class.
     * @return
     */
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(EnergyUsageEntry._ID, id);
        values.put(EnergyUsageEntry.COLUMN_DEVICE_ID, deviceId);
        values.put(EnergyUsageEntry.COLUMN_DATESTART, dateStart);
        values.put(EnergyUsageEntry.COLUMN_DATEEND, dateEnd);
        values.put(EnergyUsageEntry.COLUMN_POWER, power);
        return values;
    }

    /**
     * Create CalendarEventModel from cursor
     * @param cursor
     */
    public EnergyUsageModel(Cursor cursor) {
        setId(cursor.getLong(_id));
        setDeviceId(cursor.getLong(_deviceId));
        setDateStart(cursor.getLong(_dateStart));
        setDateEnd(cursor.getLong(_dateEnd));
        setPower(cursor.getFloat(_power));
    }

    /* Getters and setters */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDateStart() {
        return dateStart;
    }

    public void setDateStart(long dateStart) {
        this.dateStart = dateStart;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public long getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(long dateEnd) {
        this.dateEnd = dateEnd;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }
}
