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
import android.net.Uri;

import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by perok on 2/11/14.
 */
public class EnergyProvider extends ContentProvider{

    private static final String TAG = EnergyProvider.class.getName();

    private EnergyOpenHelper mHelper = null;

    private final ThreadLocal<Boolean> mIsInBatchMode = new ThreadLocal<Boolean>();

    // helper constants for use with the UriMatcher
    private static final int DEVICES_LIST = 1;
    private static final int DEVICES_ID = 2;
    private static final int ENERGY_LIST = 3;
    private static final int ENERGY_ID = 4;

    private static final UriMatcher URI_MATCHER;
    // prepare the UriMatcher
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(EnergyContract.AUTHORITY, "device", DEVICES_LIST);
        URI_MATCHER.addURI(EnergyContract.AUTHORITY, "device/#", DEVICES_ID);
        URI_MATCHER.addURI(EnergyContract.AUTHORITY, "energy", ENERGY_LIST);
        URI_MATCHER.addURI(EnergyContract.AUTHORITY, "energy/#", ENERGY_ID);
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
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //Log.v(TAG, "SORT ORDER BETCH BEFORE BETCH: " + sortOrder);
        //Log.v(TAG, "SORT ORDER: "  + BaseColumns._ID);

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        boolean useAuthorityUri = false; //TODO: Automatic notification of changes to LoadManager?
        Cursor cursor = null;
        switch (URI_MATCHER.match(uri)) {
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
            case ENERGY_LIST:
                builder.setTables(EnergyUsageModel.EnergyUsageEntry.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = EnergyContract.Energy.SORT_ORDER_DEFAULT;
                }
                break;
            case ENERGY_ID:
                builder.setTables(EnergyUsageModel.EnergyUsageEntry.TABLE_NAME);
                // limit query to one row at most:
                builder.appendWhere(EnergyContract.Energy._ID + " = " +
                    uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        //Log.v(TAG, "SORT ORDER BETCH: " + sortOrder);
        cursor =
              builder.query(
                    db,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);

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

        /*if (!(URI_MATCHER.match(uri) == DEVICES_LIST ||
                URI_MATCHER.match(uri) == ENERGY_LIST))
                throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);*/


        SQLiteDatabase db = mHelper.getWritableDatabase();

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
        int delCount = 0;
        String idStr = null;
        String where = null;
        switch (URI_MATCHER.match(uri)) {
            case DEVICES_LIST:
                delCount = db.delete(
                        DeviceModel.DeviceEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case DEVICES_ID:
                idStr = uri.getLastPathSegment();
                where = EnergyContract.Devices._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        DeviceModel.DeviceEntry.TABLE_NAME,
                        where,
                        selectionArgs);
                break;
            case ENERGY_LIST:
                delCount = db.delete(
                        EnergyUsageModel.EnergyUsageEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case ENERGY_ID:
                idStr = uri.getLastPathSegment();
                where = EnergyContract.Energy._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        EnergyUsageModel.EnergyUsageEntry.TABLE_NAME,
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
        int updateCount = 0;
        String idStr = null;
        String where = null;

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

    @Override
    public ContentProviderResult[] applyBatch(
            ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase db = mHelper.getWritableDatabase();
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
}
