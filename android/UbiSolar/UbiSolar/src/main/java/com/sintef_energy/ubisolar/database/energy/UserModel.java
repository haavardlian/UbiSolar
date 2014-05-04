package com.sintef_energy.ubisolar.database.energy;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.sintef_energy.ubisolar.model.Residence;
import com.sintef_energy.ubisolar.model.User;

/**
 * Created by Lars Erik on 04.05.2014.
 */
public class UserModel extends User implements Parcelable {

    private static final String TAG = UserModel.class.getName();

    public static interface UserEntry extends BaseColumns {
        public static final String TABLE_NAME = "facebook_user";
        public static final String COLUMN_USER_ID = "userId";
        public static final String COLUMN_NAME = "name";
    }

    public static final String[] projection = new String[]{
            UserEntry.COLUMN_USER_ID,
            UserEntry.COLUMN_NAME,
    };

    //SQL

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry.COLUMN_USER_ID +  " LONG PRIMARY KEY," + COMMA_SEP +
                    UserEntry.COLUMN_NAME + TEXT_TYPE +
                    " )";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;

    /* POJO */
    private int _userId = 0;
    private int _name = 1;

    /**
     * Create ResidenceModel with default values. All relation ID's are '-1'
     */
    public UserModel() {
        super();
        setUserId(-1);
        setName("");

    }

    /* Parcable */
    public UserModel(Parcel in) {
        readFromParcel(in);
    }

    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {

        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(getUserId());
        out.writeString(getName());
    }

    private void readFromParcel(Parcel in) {
        setUserId(in.readLong());
        setName(in.readString());
    }

    /**
     * Get all the values in ContentValues class.
     * @return
     */
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_USER_ID, getUserId());
        values.put(UserEntry.COLUMN_NAME, getName());
        return values;
    }

    /**
     * Create UserModel from cursor
     * @param cursor
     */
    public UserModel(Cursor cursor) {
        setUserId(cursor.getLong(_userId));
        setName(cursor.getString(_name));
    }

    public UserModel(long userId, String name) {
        super(userId, name);
    }

    /**
     * An ugly hack do allow jackson to serialize ResidenceModel.
     * @return new Residence
     */
    public UserModel getSerializeableResidences(){
        return new UserModel(getUserId(),getName());
    }

    @Override
    public String toString(){
        String info = "UserModel:";
        info += "\n\tID: " + getUserId();
        info += "\n\tName : " + getName();
        return info;
    }
}
