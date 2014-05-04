package com.sintef_energy.ubisolar.database.energy;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.sintef_energy.ubisolar.model.Device;

/**
 * Created by perok on 2/11/14.
 *
 * Based on the backend Device model, this class gives it all of Androids
 * powerful functionality; parcelable, ContentProvider and constructor helper methods.
 */
public class DeviceModel extends Device implements Parcelable{
    private static final String TAG = DeviceModel.class.getName();

    /* Column definitions*/
    public static interface DeviceEntry extends BaseColumns {
        public static final String TABLE_NAME = "device";
        public static final String COLUMN_USER_ID = "userId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_IS_DELETED = "is_deleted";
        public static final String COLUMN_LAST_UPDATED = "lastUpdated";
    }

    public static final String[] projection = new String[]{
            DeviceEntry._ID,
            DeviceEntry.COLUMN_USER_ID,
            DeviceEntry.COLUMN_NAME,
            DeviceEntry.COLUMN_DESCRIPTION,
            DeviceEntry.COLUMN_CATEGORY,
            DeviceEntry.COLUMN_IS_DELETED,
            DeviceEntry.COLUMN_LAST_UPDATED
    };

    /* SQL Statements*/
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DeviceEntry.TABLE_NAME + " (" +
                    DeviceEntry._ID + INTEGER_TYPE + COMMA_SEP +
                    DeviceEntry.COLUMN_USER_ID + INTEGER_TYPE + COMMA_SEP +
                    DeviceEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    DeviceEntry.COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    DeviceEntry.COLUMN_CATEGORY + INTEGER_TYPE + COMMA_SEP +
                    DeviceEntry.COLUMN_IS_DELETED + INTEGER_TYPE + COMMA_SEP +
                    DeviceEntry.COLUMN_LAST_UPDATED + INTEGER_TYPE + COMMA_SEP +
                    " PRIMARY KEY(" + DeviceEntry._ID + COMMA_SEP +
                        DeviceEntry.COLUMN_USER_ID + ")" +
                    " )";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DeviceEntry.TABLE_NAME;

    /* POJO */
    private int _id = 0;
    private int _user_id = 1;
    private int _name = 2;
    private int _description = 3;
    private int _category = 4;
    private int _is_deleted = 5;
    private int _lastUpdated = 6;

    /**
     * Create CalendarEventModel with default values. All relation ID's are '-1'
     */
    public DeviceModel() {
        super();
        setId(-1);
        setUserId(-1);
        setName("");
        setDescription("");
        setCategory(-1);
        setDeleted(false);
        setLastUpdated(-1);
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
        out.writeLong(getId());
        out.writeLong(getUserId());
        out.writeString(getName());
        out.writeString(getDescription());
        out.writeInt(getCategory());
        out.writeInt((isDeleted() ? 1 : 0));
        out.writeLong(getLastUpdated());
    }

    private void readFromParcel(Parcel in) {
        setId(in.readLong());
        setUserId(in.readLong());
        setName(in.readString());
        setDescription(in.readString());
        setCategory(in.readInt());
        setDeleted(in.readInt() != 0);
        setLastUpdated(in.readLong());
    }

    /**
     * Get all the values in ContentValues class.
     * @return
     */
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(DeviceEntry._ID, getId());
        values.put(DeviceEntry.COLUMN_USER_ID, getUserId());
        values.put(DeviceEntry.COLUMN_NAME, getName());
        values.put(DeviceEntry.COLUMN_DESCRIPTION, getDescription());
        values.put(DeviceEntry.COLUMN_CATEGORY, getCategory());
        values.put(DeviceEntry.COLUMN_IS_DELETED, (isDeleted() ? 1 : 0));
        values.put(DeviceEntry.COLUMN_LAST_UPDATED, getLastUpdated());
        return values;
    }

    /**
     * Create DeviceModel from cursor
     * @param cursor
     */
    public DeviceModel(Cursor cursor) {
        setId(cursor.getLong(_id));
        setUserId(cursor.getLong(_user_id));
        setName(cursor.getString(_name));
        setDescription(cursor.getString(_description));
        setCategory(cursor.getInt(_category));
        setDeleted(cursor.getInt(_is_deleted) != 0);
        setLastUpdated(cursor.getLong(_lastUpdated));
    }

    public DeviceModel(long id, long user_id, String name, String description, int category) {
        super(id, user_id, name, description, category, false, -1);
    }
    public DeviceModel(long id, long user_id, String name, String description, int category, boolean deleted, long last) {
        super(id, user_id, name, description, category, deleted, last);
    }

    /**
     * An ugly hack do allow jackson to serialize DeviceModel.
     * @return new Device
     */
    public Device getSerializeableDevice(){
        return new Device(getId(), getUserId(), getName(), getDescription(), getCategory(), isDeleted(), getLastUpdated());
    }

    @Override
    public String toString(){
        String info = "DeviceModel:";
        info += "\n\tID: " + getId();
        info += "\n\tUser ID: " + getUserId();
        info += "\n\tName: " + getName();
        info += "\n\tDescription: " + getDescription();
        info += "\n\tCategory: " + getCategory();
        info += "\n\tdeleted: " + isDeleted();
        info += "\n\tlastUpdated: " + getLastUpdated();
        return info;
    }
}
