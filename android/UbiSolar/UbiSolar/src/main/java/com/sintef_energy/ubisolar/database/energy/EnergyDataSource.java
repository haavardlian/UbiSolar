package com.sintef_energy.ubisolar.database.energy;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by perok on 2/11/14.
 *
 * Use this class to write all code to access data.
 *
 */
public class EnergyDataSource {

    private static final String TAG = EnergyDataSource.class.getName();

    /**
     * Add EnergyUsageModel to database.
     *
     * @param resolver Resolver for the Context.
     * @param euModel The EnergyUsageModel to add.
     * @return new Uri to resource, or Null if something went wrong.
     */
    public static Uri addEnergyModel(ContentResolver resolver, EnergyUsageModel euModel){
        Uri.Builder builder = EnergyContract.Energy.CONTENT_URI.buildUpon();

        Uri uri = resolver.insert(builder.build(), euModel.getContentValues());

        return uri;
    }

    public static int getEnergyModelSize(ContentResolver resolver){
        ArrayList<EnergyUsageModel> euModels = new ArrayList<>();

        Uri.Builder builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
        //ContentUris.appendId(builder, id);
        Cursor cursor = resolver.query(
                builder.build(),
                EnergyContract.Energy.PROJECTION_ALL,
                null,
                null,
                null
        );

        if(cursor == null){
            return 0;
        }

        int size = cursor.getCount();

        cursor.close();

        return size;
    }

    public static ArrayList<EnergyUsageModel> getEnergyModels(ContentResolver resolver, long from, long to){

        ArrayList<EnergyUsageModel> euModels = new ArrayList<>();

        Uri.Builder builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
        //ContentUris.appendId(builder, id);
        Cursor cursor = resolver.query(
                builder.build(),
                EnergyContract.Energy.PROJECTION_ALL,
                EnergyUsageModel.EnergyUsageEntry.COLUMN_TIMESTAMP + " >= " + from +
                    " AND " + EnergyUsageModel.EnergyUsageEntry.COLUMN_TIMESTAMP + " <= " + to,
                null,
                null
        );

        if(cursor == null){
            return null;
        }
        else if(cursor.getCount() < 1){
            cursor.close();
            return null;
        }
        else {

        }

        cursor.moveToFirst();
        euModels.add(new EnergyUsageModel(cursor, false));

        while (cursor.moveToNext())
            euModels.add(new EnergyUsageModel(cursor, false));

        cursor.close();

        return euModels;
    }

    /**
     * Get specific EnergyModel.
     *
     * @param resolver
     * @param id
     * @return
     */
    public static DeviceModel getEnergyModel(ContentResolver resolver, long id){

        Uri.Builder builder = EnergyContract.Devices.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, id);
        Cursor cursor = resolver.query(
                builder.build(),
                EnergyContract.Devices.PROJECTION_ALL,
                null,
                null,
                null
        );

        if(cursor == null){
            return null;
        }
        else if(cursor.getCount() < 1){
            cursor.close();
            return null;
        }
        else {

        }

        cursor.moveToFirst();
        DeviceModel crm = new DeviceModel(cursor);
        cursor.close();

        return crm;
    }

    /*
    *Insert device into database
    */
    public static void  insertDevice(ContentResolver resolver, DeviceModel model){
        resolver.insert(EnergyContract.Devices.CONTENT_URI, model.getContentValues());
    }

    /*
    *Delete all devices from database
    */
    public static void deleteAll(ContentResolver resolver){
        resolver.delete(EnergyContract.Devices.CONTENT_URI, null, null);
    }

    public static ArrayList<DeviceModel> getAllDeviceModels(ContentResolver resolver){
        ArrayList<DeviceModel> deviceModels = new ArrayList<>();

        Cursor cursor = resolver.query(
                EnergyContract.Devices.CONTENT_URI,
                EnergyContract.Devices.PROJECTION_ALL,
                null,
                null,
                null
        );

        if(cursor == null){
            cursor.close();
            return null;
        }
        else if(cursor.getCount() < 1){
            cursor.close();
            return null;
        }
        else {

        }

        cursor.moveToFirst();
        deviceModels.add(new DeviceModel(cursor));

        while (cursor.moveToNext())
            deviceModels.add(new DeviceModel(cursor));

        cursor.close();
        return deviceModels;
     }

    public static int addBatchEnergyModel(ContentResolver resolver, ArrayList<EnergyUsageModel> usageModels){
        //AsyncQueryHandler handler = new AsyncQueryHandler(resolver) {};

        if(usageModels.size() < 1)
            return 0;

        ContentValues[] values = new ContentValues[usageModels.size()];
        int i = 0;
        for(EnergyUsageModel dm : usageModels) {
            values[i] = dm.getContentValues();
            i++;
        }

        Uri.Builder builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
        int n = resolver.bulkInsert(
                builder.build(),
                values
            );

        if(n != values.length)
            Log.e(TAG, "addBatchEnergyModel: Tried adding: " +values.length + ". Total added was: " + n);

        return n;
    }

    public static int addBatchDeviceModel(ContentResolver resolver, ArrayList<DeviceModel> deviceModels){
        //AsyncQueryHandler handler = new AsyncQueryHandler(resolver) {};

        if(deviceModels.size() < 1)
            return 0;

        ContentValues[] values = new ContentValues[deviceModels.size()];
        int i = 0;
        for(DeviceModel dm : deviceModels) {
            values[i] = dm.getContentValues();
            i++;
        }

        Uri.Builder builder = EnergyContract.Devices.CONTENT_URI.buildUpon();
        int n = resolver.bulkInsert(
                builder.build(),
                values
            );

        if(n != values.length)
            Log.e(TAG, "addBatchDeviceModel: Tried adding: " +values.length + ". Total added was: " + n);

        return n;
    }

    public static void editDevice(ContentResolver contentResolver, DeviceModel dm){
        int i = contentResolver.update(EnergyContract.Devices.CONTENT_URI, dm.getContentValues(),
                DeviceModel.DeviceEntry._ID + "=?",new String[]{"" + dm.getId()});
    }

    public static void getDevice(ContentResolver contentResolver, DeviceModel dm){
        contentResolver.query(EnergyContract.Devices.CONTENT_URI,
                EnergyContract.Devices.PROJECTION_ALL,
                DeviceModel.DeviceEntry._ID + "=",
                new String[]{""+dm.getId()},
                null);
    }


    /* SYNC OPERATIONS */

    /**
     *
     * Retrieves all device models (included deleted ones) with lastUpdated >= timestamp.
     *
     * @param resolver
     * @param timestamp
     * @return
     */
    public static ArrayList<DeviceModel> getAllSyncDevices(ContentResolver resolver, long timestamp){
        ArrayList<DeviceModel> deviceModels = new ArrayList<>();

        Uri.Builder builder = EnergyContract.Devices.CONTENT_URI.buildUpon();
        builder.appendPath(EnergyContract.DELETE); //Get deleted data aswell

        Cursor cursor = resolver.query(
                builder.build(),
                EnergyContract.Devices.PROJECTION_ALL,
                DeviceModel.DeviceEntry.COLUMN_LAST_UPDATED + " >= ?",
                new String[]{""+timestamp},
                null
        );

        if(cursor == null){
            cursor.close();
            return null;
        }
        else if(cursor.getCount() < 1){
            cursor.close();
            return deviceModels;
        }

        cursor.moveToFirst();

        Log.v(TAG, "getAllSyncDevices: # of models: " + cursor.getCount());

        deviceModels.add(new DeviceModel(cursor));
        while (cursor.moveToNext())
            deviceModels.add(new DeviceModel(cursor));

        cursor.close();
        return deviceModels;
     }

    public static ArrayList<EnergyUsageModel> getAllSyncUsage(ContentResolver resolver, long timestamp){
        ArrayList<EnergyUsageModel> deviceModels = new ArrayList<>();

        Uri.Builder builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
        builder.appendPath(EnergyContract.DELETE); //Get deleted data also

        Cursor cursor = resolver.query(
                builder.build(),
                EnergyContract.Energy.PROJECTION_ALL,
                EnergyUsageModel.EnergyUsageEntry.COLUMN_LAST_UPDATED + " >= ?",
                new String[]{""+timestamp},
                null
        );

        if(cursor == null){
            cursor.close();
            return null;
        }
        else if(cursor.getCount() < 1){
            cursor.close();
            return deviceModels;
        }

        cursor.moveToFirst();

        deviceModels.add(new EnergyUsageModel(cursor, false));

        Log.v(TAG, "getAllSyncDevices: # of models: " + cursor.getCount());

        while (cursor.moveToNext())
            deviceModels.add(new EnergyUsageModel(cursor, false));

        cursor.close();
        return deviceModels;
     }
    /**
     * Batch deletes the given DeviceModels. Only for use in sync cases.
     *
     * @param contentResolver
     * @param deviceModels
     * @return success/ failure
     */
    public static boolean batchDeleteDeviceSyncOp(ContentResolver contentResolver, ArrayList<DeviceModel> deviceModels){
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        ContentProviderOperation operation;

        for (DeviceModel item : deviceModels) {

            operation = ContentProviderOperation
                    .newDelete(EnergyContract.Devices.CONTENT_URI)
                    .withSelection(DeviceModel.DeviceEntry._ID + " = ?", new String[]{"" + item.getId()})
                    .build();

            operations.add(operation);
        }

        try {
            contentResolver.applyBatch(EnergyContract.AUTHORITY, operations);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public static boolean batchDeleteUsageSyncOp(ContentResolver contentResolver, ArrayList<EnergyUsageModel> deviceModels){
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        ContentProviderOperation operation;

        for (EnergyUsageModel item : deviceModels) {

            operation = ContentProviderOperation
                    .newDelete(EnergyContract.Energy.CONTENT_URI)
                    .withSelection(EnergyUsageModel.EnergyUsageEntry._ID + " = ?", new String[]{"" + item.getId()})
                    .build();

            operations.add(operation);
        }

        try {
            contentResolver.applyBatch(EnergyContract.AUTHORITY, operations);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
