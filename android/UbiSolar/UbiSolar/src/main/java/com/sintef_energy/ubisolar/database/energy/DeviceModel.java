package com.sintef_energy.ubisolar.database.energy;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.sintef_energy.ubisolar.model.Device;

/**
 * Created by perok on 2/11/14.
 */
public class DeviceModel extends Device implements Parcelable{
    private static final String TAG = DeviceModel.class.getName();

    /* Column definitions*/
    public static interface DeviceEntry extends BaseColumns {
        public static final String TABLE_NAME = "device";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_IS_TOTAL = "is_total";
    }

    public static final String[] projection = new String[]{
            DeviceEntry._ID,
            DeviceEntry.COLUMN_USER_ID,
            DeviceEntry.COLUMN_NAME,
            DeviceEntry.COLUMN_DESCRIPTION,
            DeviceEntry.COLUMN_CATEGORY,
            DeviceEntry.COLUMN_IS_TOTAL
    };

    /* SQL Statements*/
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DeviceEntry.TABLE_NAME + " (" +
                    DeviceEntry._ID + " INTEGER PRIMARY KEY," +
                    DeviceEntry.COLUMN_USER_ID + INTEGER_TYPE + COMMA_SEP +
                    DeviceEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    DeviceEntry.COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    DeviceEntry.COLUMN_CATEGORY + INTEGER_TYPE + COMMA_SEP +
                    DeviceEntry.COLUMN_IS_TOTAL + INTEGER_TYPE +
                    " )";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DeviceEntry.TABLE_NAME;

    /* POJO */
    private int _id = 0;
    private int _user_id = 1;
    private int _name = 2;
    private int _description = 3;
    private int _category = 4;
    private int _is_total = 5;


    /**
     * Create CalendarEventModel with default values. All relation ID's are '-1'
     */
    public DeviceModel() {
        setDevice_id(-1);
        setUser_id(-1);
        setName("");
        setDescription("");
        setCategory(-1);
        setIsTotal(false);
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
        out.writeLong(getDevice_id());
        out.writeLong(getUser_id());
        out.writeString(getName());
        out.writeString(getDescription());
        out.writeInt(getCategory());
        out.writeInt((isTotal() ? 1 : 0));
    }

    private void readFromParcel(Parcel in) {
        setDevice_id(in.readLong());
        setUser_id(in.readLong());
        setName(in.readString());
        setDescription(in.readString());
        setCategory(in.readInt());
        setIsTotal(in.readInt() != 0);
    }

    /**
     * Get all the values in ContentValues class.
     * @return
     */
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(DeviceEntry._ID, getDevice_id());
        values.put(DeviceEntry.COLUMN_USER_ID, getUser_id());
        values.put(DeviceEntry.COLUMN_NAME, getName());
        values.put(DeviceEntry.COLUMN_DESCRIPTION, getDescription());
        values.put(DeviceEntry.COLUMN_CATEGORY, getCategory());
        values.put(DeviceEntry.COLUMN_IS_TOTAL, (isTotal() ? 1 : 0));
        return values;
    }

    /**
     * Create DeviceModel from cursor
     * @param cursor
     */
    public DeviceModel(Cursor cursor) {
        setDevice_id(cursor.getLong(_id));
        setUser_id(cursor.getLong(_user_id));
        setName(cursor.getString(_name));
        setDescription(cursor.getString(_description));
        setCategory(cursor.getInt(_category));
        setIsTotal(cursor.getInt(_is_total) != 0);
    }

    public DeviceModel(long device_id, String name, String description, long user_id,
                       int category, boolean isTotal) {
        super(device_id, name, description, user_id, category, isTotal);
    }
}
