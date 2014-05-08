package com.sintef_energy.ubisolar.database.energy;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by perok on 2/11/14.
 *
 * TODO
 * ContentProvder is not thread safee. SQLiteDatabase is thread safe.
 * Should the providers CRUD method be implemented with synchronized? Will give a overhead, but
 * will possibly avoid bugs.
 *
 * TODO Use UID on all searches
 *
 */
public class EnergyProvider extends ContentProvider{

    private static final String TAG = EnergyProvider.class.getName();
    private EnergyOpenHelper mHelper = null;
    private final ThreadLocal<Boolean> mIsInBatchMode = new ThreadLocal<>();

    // helper constants for use with the UriMatcher
    private static final int DEVICES_LIST_DELETE = 9;
    private static final int DEVICES_LIST = 1;
    private static final int DEVICES_ID = 2;
    private static final int ENERGY_LIST_DELETE = 10;
    private static final int ENERGY_LIST = 3;
    private static final int ENERGY_ID = 4;
    private static final int ENERGY_DAY_LIST = 5;
    private static final int ENERGY_WEEK_LIST = 6;
    private static final int ENERGY_MONTH_LIST = 7;
    private static final int ENERGY_YEAR_LIST = 8;


    private static ContentValues deleteValues;

    private static final UriMatcher URI_MATCHER;
    // prepare the UriMatcher
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(EnergyContract.AUTHORITY, "device", DEVICES_LIST);
        URI_MATCHER.addURI(EnergyContract.AUTHORITY, "device/#", DEVICES_ID);
        URI_MATCHER.addURI(EnergyContract.AUTHORITY, "device/" + EnergyContract.DELETE, DEVICES_LIST_DELETE);
        URI_MATCHER.addURI(EnergyContract.AUTHORITY, "energy", ENERGY_LIST);
        URI_MATCHER.addURI(EnergyContract.AUTHORITY, "energy/#", ENERGY_ID);
        URI_MATCHER.addURI(EnergyContract.AUTHORITY, "energy/" + EnergyContract.Energy.Date.Day, ENERGY_DAY_LIST);
        URI_MATCHER.addURI(EnergyContract.AUTHORITY, "energy/" + EnergyContract.Energy.Date.Week, ENERGY_WEEK_LIST);
        URI_MATCHER.addURI(EnergyContract.AUTHORITY, "energy/" + EnergyContract.Energy.Date.Month, ENERGY_MONTH_LIST);
        URI_MATCHER.addURI(EnergyContract.AUTHORITY, "energy/" + EnergyContract.Energy.Date.Year, ENERGY_YEAR_LIST);
        URI_MATCHER.addURI(EnergyContract.AUTHORITY, "energy/" + EnergyContract.DELETE, ENERGY_LIST_DELETE);

        deleteValues = new ContentValues();
        deleteValues.put(DeviceModel.DeviceEntry.COLUMN_IS_DELETED, true);
   }

    @Override
    public boolean onCreate() {
        mHelper = new EnergyOpenHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch(URI_MATCHER.match(uri)){
            case DEVICES_LIST:
                return EnergyContract.Devices.CONTENT_TYPE;
            case DEVICES_ID:
                return EnergyContract.Devices.CONTENT_ITEM_TYPE;
            case ENERGY_LIST:
                return EnergyContract.Energy.CONTENT_TYPE;
            case ENERGY_ID:
                return EnergyContract.Energy.CONTENT_ITEM_TYPE;
            case ENERGY_DAY_LIST:
                return EnergyContract.Energy.CONTENT_TYPE;
            case ENERGY_WEEK_LIST:
                return EnergyContract.Energy.CONTENT_TYPE;
            case ENERGY_MONTH_LIST:
                return EnergyContract.Energy.CONTENT_TYPE;
            case ENERGY_YEAR_LIST:
                return EnergyContract.Energy.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        if(db == null) return null;

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        boolean useAuthorityUri = false; //TODO: Automatic notification of changes to LoadManager?
        Cursor cursor;

        //Used by day, month, year
        String rawSql = null;

        boolean deleteData = false;

        switch (URI_MATCHER.match(uri)) {
            // We want the deleted data also
            case DEVICES_LIST_DELETE:
                deleteData = true;
            case DEVICES_LIST:
                builder.setTables(DeviceModel.DeviceEntry.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = EnergyContract.Devices.SORT_ORDER_DEFAULT;
                }
                break;
            case DEVICES_ID:
                builder.setTables(DeviceModel.DeviceEntry.TABLE_NAME);
                // limit query to one row at most:
                builder.appendWhere(EnergyContract.Devices._ID + " = " +
                    uri.getLastPathSegment());
                break;
            case ENERGY_LIST_DELETE:
                deleteData = true;
            case ENERGY_LIST:
                builder.setTables(EnergyUsageModel.EnergyUsageEntry.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = EnergyContract.Energy.SORT_ORDER_DEFAULT;
                }
                //rawSql = generateRawDateSql("%Y-%m-%d %H", selection);
                break;
            case ENERGY_ID:
                builder.setTables(EnergyUsageModel.EnergyUsageEntry.TABLE_NAME);
                // limit query to one row at most:
                builder.appendWhere(EnergyContract.Energy._ID + " = " +
                    uri.getLastPathSegment());
                break;
            case ENERGY_DAY_LIST:
                rawSql = generateRawDateSql("%Y-%m-%d", selection);
                break;
            case ENERGY_WEEK_LIST:
                rawSql = generateRawDateSql("%Y-%W", selection);
                break;
            case ENERGY_MONTH_LIST:
                rawSql = generateRawDateSql("%Y-%m", selection);
                break;
            case ENERGY_YEAR_LIST:
                rawSql = generateRawDateSql("%Y", selection);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if(rawSql == null) {
            if (selection == null) {
                if (!deleteData)
                    selection = selectionAvoidDeleteBit;
            } else if (!selection.equals("")) {
                selection = "(" + selection + ") AND " + selectionAvoidDeleteBit;
            }

            cursor =
                    builder.query(
                            db,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
        }
        else
            cursor = db.rawQuery(rawSql, selectionArgs);

        // if we want to be notified of any changes:
        if (useAuthorityUri) {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    EnergyContract.CONTENT_URI);
        }
        else {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    uri);
        }

        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        /*TODO add: if (!(URI_MATCHER.match(uri) == DEVICES_LIST ||
                URI_MATCHER.match(uri) == ENERGY_LIST))
                throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);*/
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if(db == null) return null;

        long id = -1;

        if (URI_MATCHER.match(uri) == DEVICES_LIST) {
            id = db.insert(
                    DeviceModel.DeviceEntry.TABLE_NAME,
                    null,
                    values);
        }
        else if (URI_MATCHER.match(uri) == ENERGY_LIST) {
            id = db.insert(
                    EnergyUsageModel.EnergyUsageEntry.TABLE_NAME,
                    null,
                    values);
        }

        return getUriForId(id, uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if(db == null) return -1;

        int delCount;
        String idStr;
        String where;

        deleteValues.put(DeviceModel.DeviceEntry.COLUMN_LAST_UPDATED, System.currentTimeMillis() / 1000L);

        switch (URI_MATCHER.match(uri)) {
            /* ONLY DELETE ON THESE TWO */
            case DEVICES_LIST_DELETE:
                delCount = db.delete(
                        DeviceModel.DeviceEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case ENERGY_LIST_DELETE:
                delCount = db.delete(
                        EnergyUsageModel.EnergyUsageEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            /* Sets deletebit in these*/
            case DEVICES_LIST:
                delCount = db.update(
                        DeviceModel.DeviceEntry.TABLE_NAME,
                        deleteValues,
                        selection,
                        selectionArgs);
                break;
            case DEVICES_ID:
                idStr = uri.getLastPathSegment();
                where = EnergyContract.Devices._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }

                delCount = db.update(
                        DeviceModel.DeviceEntry.TABLE_NAME,
                        deleteValues,
                        where,
                        selectionArgs);
                break;
            case ENERGY_LIST:
                delCount = db.update(
                        EnergyUsageModel.EnergyUsageEntry.TABLE_NAME,
                        deleteValues,
                        selection,
                        selectionArgs);
                break;
            case ENERGY_ID:
                idStr = uri.getLastPathSegment();
                where = EnergyContract.Energy._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.update(
                        EnergyUsageModel.EnergyUsageEntry.TABLE_NAME,
                        deleteValues,
                        where,
                        selectionArgs);
                break;
            default:
                // no support for deleting photos or entities -
                // photos are deleted by a trigger when the item is deleted
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (delCount > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return delCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if(db == null) return -1;

        int updateCount;
        String idStr;
        String where;

        switch (URI_MATCHER.match(uri)) {
            case DEVICES_LIST:
                updateCount = db.update(
                        DeviceModel.DeviceEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case DEVICES_ID:
                idStr = uri.getLastPathSegment();
                where = EnergyContract.Devices._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        DeviceModel.DeviceEntry.TABLE_NAME,
                        values,
                        where,
                        selectionArgs);
                break;
            case ENERGY_LIST:
                updateCount = db.update(
                        EnergyUsageModel.EnergyUsageEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case ENERGY_ID:
                idStr = uri.getLastPathSegment();
                where = EnergyContract.Energy._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        EnergyUsageModel.EnergyUsageEntry.TABLE_NAME,
                        values,
                        where,
                        selectionArgs);
                break;
            default:
                // no support for updating photos or entities!
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (updateCount > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updateCount;
    }

    /*
    * For fast insert. ApplyBatch or bulkInsert?
    * http://stackoverflow.com/questions/5596354/insertion-of-thousands-of-contact-entries-using-applybatch-is-slow
    * */

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        if(db == null) return -1;

        final int match = URI_MATCHER.match(uri);
        int numInserted = 0;
        switch(match){
            case ENERGY_LIST:
                mIsInBatchMode.set(true);
                db.beginTransaction();
                try {
                    //standard SQL insert statement, that can be reused
                    SQLiteStatement insert =
                            db.compileStatement("INSERT OR REPLACE INTO " + EnergyUsageModel.EnergyUsageEntry.TABLE_NAME
                                    + "(" + EnergyUsageModel.EnergyUsageEntry._ID + ","
                                    + EnergyUsageModel.EnergyUsageEntry.COLUMN_DEVICE_ID + ","
                                    + EnergyUsageModel.EnergyUsageEntry.COLUMN_TIMESTAMP + ","
                                    + EnergyUsageModel.EnergyUsageEntry.COLUMN_POWER + ","
                                    + EnergyUsageModel.EnergyUsageEntry.COLUMN_IS_DELETED + ","
                                    + EnergyUsageModel.EnergyUsageEntry.COLUMN_LAST_UPDATED + ")"
                                    +" values " + "(?,?,?,?,?,?)");
                    for (ContentValues value : values){
                        //bind the 1-indexed ?'s to the values specified
                        insert.bindLong(1, value.getAsLong(EnergyUsageModel.EnergyUsageEntry._ID));
                        insert.bindLong(2, value.getAsLong(EnergyUsageModel.EnergyUsageEntry.COLUMN_DEVICE_ID));
                        insert.bindLong(3, value.getAsLong(EnergyUsageModel.EnergyUsageEntry.COLUMN_TIMESTAMP));
                        insert.bindDouble(4, value.getAsDouble(EnergyUsageModel.EnergyUsageEntry.COLUMN_POWER));
                        insert.bindLong(5, value.getAsLong(EnergyUsageModel.EnergyUsageEntry.COLUMN_IS_DELETED));
                        insert.bindLong(6, value.getAsLong(EnergyUsageModel.EnergyUsageEntry.COLUMN_LAST_UPDATED));
                        insert.execute();

                        db.yieldIfContendedSafely();
                    }
                    db.setTransactionSuccessful();
                    insert.close();
                    numInserted = values.length;
                } finally {
                    mIsInBatchMode.remove();

                    db.endTransaction();
                    getContext().getContentResolver().notifyChange(EnergyContract.CONTENT_URI, null);
                }
                return numInserted;
            case DEVICES_LIST:
                mIsInBatchMode.set(true);
                db.beginTransaction();
                try {
                    //standard SQL insert statement, that can be reused
                    SQLiteStatement insert =
                            db.compileStatement("INSERT OR REPLACE INTO " + DeviceModel.DeviceEntry.TABLE_NAME
                                    + "(" + DeviceModel.DeviceEntry._ID + ","
                                    + DeviceModel.DeviceEntry.COLUMN_USER_ID + ","
                                    + DeviceModel.DeviceEntry.COLUMN_NAME + ","
                                    + DeviceModel.DeviceEntry.COLUMN_DESCRIPTION + ","
                                    + DeviceModel.DeviceEntry.COLUMN_CATEGORY + ","
                                    + DeviceModel.DeviceEntry.COLUMN_IS_DELETED + ","
                                    + DeviceModel.DeviceEntry.COLUMN_LAST_UPDATED + ")"
                                    +" VALUES " + "(?,?,?,?,?,?,?)");
                    for (ContentValues value : values){
                        //bind the 1-indexed ?'s to the values specified
                        insert.bindLong(1, value.getAsLong(DeviceModel.DeviceEntry._ID));
                        insert.bindLong(2, value.getAsLong(DeviceModel.DeviceEntry.COLUMN_USER_ID));
                        insert.bindString(3, value.getAsString(DeviceModel.DeviceEntry.COLUMN_NAME));
                        insert.bindString(4, value.getAsString(DeviceModel.DeviceEntry.COLUMN_DESCRIPTION));
                        insert.bindLong(5, value.getAsInteger(DeviceModel.DeviceEntry.COLUMN_CATEGORY));
                        insert.bindLong(6, value.getAsInteger(DeviceModel.DeviceEntry.COLUMN_IS_DELETED));
                        insert.bindLong(7, value.getAsInteger(DeviceModel.DeviceEntry.COLUMN_LAST_UPDATED));
                        insert.execute();

                        db.yieldIfContendedSafely();
                    }
                    db.setTransactionSuccessful();
                    insert.close();
                    numInserted = values.length;
                } finally {
                    mIsInBatchMode.remove();

                    db.endTransaction();
                    getContext().getContentResolver().notifyChange(EnergyContract.CONTENT_URI, null);
                }
                return numInserted;
            default:
                throw new UnsupportedOperationException("unsupported uri: " + uri);
        }
    }

    @Override
    public ContentProviderResult[] applyBatch(
            ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if(db == null) return null;

        mIsInBatchMode.set(true);
        // the next line works because SQLiteDatabase
        // uses a thread local SQLiteSession object for
        // all manipulations
        db.beginTransaction();
        try {
            final ContentProviderResult[] retResult = super.applyBatch(operations);
            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(EnergyContract.CONTENT_URI, null);

            return retResult;
        }
        finally {
            mIsInBatchMode.remove();
            db.endTransaction();
        }
    }

    private boolean isInBatchMode() {
        return mIsInBatchMode.get() != null && mIsInBatchMode.get();
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            if (!isInBatchMode()) {
                    // notify all listeners of changes:
                    getContext().getContentResolver().notifyChange(itemUri, null);
                }

            return itemUri;
         }

        // s.th. went wrong:
        throw new SQLException("Problem while inserting into uri: " + uri);
   }


    private static final String selectionAvoidDeleteBit = DeviceModel.DeviceEntry.COLUMN_IS_DELETED + "<>1 ";

    private String generateRawDateSql(String date, String where){

        //Time to aggregate on
        String time =  "strftime(\'" + date + "\', datetime(`" +
                EnergyUsageModel.EnergyUsageEntry.COLUMN_TIMESTAMP + "`, 'unixepoch'))";

        //Unixtime
        String time2 =  "strftime(\'%s\', datetime(`" +
                EnergyUsageModel.EnergyUsageEntry.COLUMN_TIMESTAMP + "`, 'unixepoch'))";

        String betweenTime = "strftime('%Y-%m-%d %H:%M', datetime(`" +
                EnergyUsageModel.EnergyUsageEntry.COLUMN_TIMESTAMP + "`, 'unixepoch', 'localtime'))";


//        String statement = "SELECT * FROM (";

        String statement = "SELECT " + EnergyUsageModel.EnergyUsageEntry._ID + ", "
                        + EnergyUsageModel.EnergyUsageEntry.COLUMN_DEVICE_ID + ", "
                        + time2 + " As `month`, "
                        + "Sum(" + EnergyUsageModel.EnergyUsageEntry.COLUMN_POWER + ") As `amount` "
                        + "FROM " + EnergyUsageModel.EnergyUsageEntry.TABLE_NAME + " ";

        if(!where.equals(""))
            statement += "WHERE (" + where + ") AND " + selectionAvoidDeleteBit;
        else
            statement += "WHERE " + selectionAvoidDeleteBit;


        statement += "GROUP BY " + time + ", "
            + EnergyUsageModel.EnergyUsageEntry.COLUMN_DEVICE_ID + " "
            + "ORDER BY `month` ASC";

//        statement += " limit 15) ORDER BY `month` ASC";

        return statement;
    }
}
