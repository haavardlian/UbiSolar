package com.sintef_energy.ubisolar.database.energy;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import com.sintef_energy.ubisolar.model.Residence;

/**
 * Created by Lars Erik on 02.05.2014.
 */
public class ResidenceModel extends Residence implements Parcelable {
    private static final String TAG = ResidenceModel.class.getName();

    public static interface ResidenceEntry extends BaseColumns {
        public static final String TABLE_NAME = "residence";
        public static final String COLUMN_HOUSE_ID = "houseName";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_RESIDENTS = "residents";
        public static final String COLUMN_AREA = "area";
        public static final String COLUMN_ZIPCODE = "zipCode";
        public static final String COLUMN_ENERGY_CLASS = "energyClass";
        public static final String COLUM_USER_ID = "userID";
    }

    public static final String[] projection = new String[]{
            ResidenceEntry.COLUMN_HOUSE_ID,
            ResidenceEntry.COLUMN_DESCRIPTION,
            ResidenceEntry.COLUMN_RESIDENTS,
            ResidenceEntry.COLUMN_AREA,
            ResidenceEntry.COLUMN_ZIPCODE,
            ResidenceEntry.COLUMN_ENERGY_CLASS,
            ResidenceEntry.COLUM_USER_ID
    };

    //SQL

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String LONG_TYPE = " LONG";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ResidenceEntry.TABLE_NAME + " (" +
                    ResidenceEntry.COLUMN_HOUSE_ID +  " STRING PRIMARY KEY," + COMMA_SEP +
                    ResidenceEntry.COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    ResidenceEntry.COLUMN_RESIDENTS + INTEGER_TYPE + COMMA_SEP +
                    ResidenceEntry.COLUMN_AREA + INTEGER_TYPE + COMMA_SEP +
                    ResidenceEntry.COLUMN_ZIPCODE + INTEGER_TYPE + COMMA_SEP +
                    ResidenceEntry.COLUMN_ENERGY_CLASS + TEXT_TYPE + COMMA_SEP +
                    ResidenceEntry.COLUM_USER_ID + LONG_TYPE +
                    " )";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ResidenceEntry.TABLE_NAME;

    /* POJO */
    private int _houseId = 0;
    private int _description = 1;
    private int _residents = 2;
    private int _area = 3;
    private int _zipCode = 4;
    private int _energyClass = 5;
    private int _userId = 6;

    /**
     * Create ResidenceModel with default values. All relation ID's are '-1'
     */
    public ResidenceModel() {
        super();
        setHouseName("");
        setDescription("");
        setResidents(-1);
        setArea(-1);
        setZipCode(-1);
        setEnergyClass(' ');
        setUserId(-1);
    }

    /* Parcable */
    public ResidenceModel(Parcel in) {
        readFromParcel(in);
    }

    public static final Parcelable.Creator<ResidenceModel> CREATOR = new Parcelable.Creator<ResidenceModel>() {

        public ResidenceModel createFromParcel(Parcel in) {
            return new ResidenceModel(in);
        }

        public ResidenceModel[] newArray(int size) {
            return new ResidenceModel[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getHouseName());
        out.writeString(getDescription());
        out.writeInt(getResidents());
        out.writeInt(getArea());
        out.writeInt(getZipCode());
        out.writeString(String.valueOf(getEnergyClass()));
        out.writeLong(getUserId());
    }

    private void readFromParcel(Parcel in) {
        setHouseName(in.readString());
        setDescription(in.readString());
        setResidents(in.readInt());
        setArea(in.readInt());
        setZipCode(in.readInt());
        setEnergyClass(in.readString().charAt(0));
        setUserId(in.readLong());
    }

    /**
     * Get all the values in ContentValues class.
     * @return
     */
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(ResidenceEntry.COLUMN_HOUSE_ID, getHouseName());
        values.put(ResidenceEntry.COLUMN_DESCRIPTION, getDescription());
        values.put(ResidenceEntry.COLUMN_RESIDENTS, getResidents());
        values.put(ResidenceEntry.COLUMN_AREA, getArea());
        values.put(ResidenceEntry.COLUMN_ZIPCODE, getZipCode());
        values.put(ResidenceEntry.COLUMN_ENERGY_CLASS, String.valueOf(getEnergyClass()));
        values.put(ResidenceEntry.COLUM_USER_ID, getUserId());
        return values;
    }

    /**
     * Create ResidenceModel from cursor
     * @param cursor
     */
    public ResidenceModel(Cursor cursor) {
        setHouseName(cursor.getString(_houseId));
        setDescription(cursor.getString(_description));
        setResidents(cursor.getInt(_residents));
        setArea(cursor.getInt(_area));
        setZipCode(cursor.getInt(_zipCode));
        setEnergyClass(cursor.getString(_energyClass));
        setUserId(cursor.getLong(_userId));
    }

    public ResidenceModel(String houseId, String description, int residents, int area,
                          int zipCode, char energyClass, long userId) {
        super(houseId, description, residents, area, zipCode, energyClass, userId);
    }
 /*   public ResidenceModel(int id, long user_id, String name, String description, int category, boolean deleted, long last) {
        super(id, user_id, name, description, category, deleted, last);
    }*/

    /**
     * An ugly hack do allow jackson to serialize ResidenceModel.
     * @return new Residence
     */
    public Residence getSerializeableResidences(){
        return new Residence(getHouseName(), getDescription(), getResidents(), getArea(),
                getZipCode(), getEnergyClass(), getUserId());
    }

    @Override
    public String toString(){
        String info = "ResidenceModel:";
        info += "\n\tID: " + getHouseName();
        info += "\n\tDescription : " + getDescription();
        info += "\n\tResidents : " + getResidents();
        info += "\n\tSize : " + getArea();
        info += "\n\tZip code: " + getZipCode();
        info += "\n\tEnergy class: " + getEnergyClass();
        info += "\n\tUser id: " + getUserId();
        return info;
    }
}
