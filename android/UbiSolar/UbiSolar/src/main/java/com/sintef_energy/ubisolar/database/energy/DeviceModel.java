package com.sintef_energy.ubisolar.database.energy;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

/**
 * Created by perok on 2/11/14.
 */
public class DeviceModel implements Parcelable{
    private static final String TAG = DeviceModel.class.getName();

    /* Column definitions*/
    public static interface DeviceEntry extends BaseColumns {
        public static final String TABLE_NAME = "devices";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
    }

    public static final String[] projection = new String[]{
            DeviceEntry._ID,
            DeviceEntry.COLUMN_NAME,
            DeviceEntry.COLUMN_DESCRIPTION
    };

    /* SQL Statements*/
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DeviceEntry.TABLE_NAME + " (" +
                    DeviceEntry._ID + " INTEGER PRIMARY KEY," +
                    DeviceEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    DeviceEntry.COLUMN_DESCRIPTION + TEXT_TYPE +
                    " )";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DeviceEntry.TABLE_NAME;

    /* POJO */
    private long id;
    private int _id = 0;
    private String name;
    private int _name = 1;
    private String description;
    private int _description = 2;


    /**
     * Create CalendarEventModel with default values. All relation ID's are '-1'
     */
    public DeviceModel() {
        setId(-1);
        setName("");
        setDescription("");
    }

    /* Parcable */
    public DeviceModel(Parcel in) {
        readFromParcel(in);
    }

    public static final Parcelable.Creator<DeviceModel> CREATOR = new Parcelable.Creator<DeviceModel>() {

        public DeviceModel createFromParcel(Parcel in) {
            return new DeviceModel(in);
        }

        public DeviceModel[] newArray(int size) {
            return new DeviceModel[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(name);
        out.writeString(description);
    }

    private void readFromParcel(Parcel in) {
        setId(in.readLong());
        setName(in.readString());
        setDescription(in.readString());
    }

    /**
     * Get all the values in ContentValues class.
     * @return
     */
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(DeviceEntry._ID, id);
        values.put(DeviceEntry.COLUMN_NAME, name);
        values.put(DeviceEntry.COLUMN_DESCRIPTION, description);
        return values;
    }


    /**
     * Create CalendarEventModel from cursor
     * @param cursor
     */
    public DeviceModel(Cursor cursor) {
        setId(cursor.getLong(_id));
        setName(cursor.getString(_name));
        setDescription(cursor.getString(_description));
    }

    /* Getters and setters */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
