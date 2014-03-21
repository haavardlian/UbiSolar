package com.sintef_energy.ubisolar.database.energy;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
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
     * @return new Uri to resource.
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
                EnergyUsageModel.EnergyUsageEntry.COLUMN_DATETIME + " >= " + from +
                    " AND " + EnergyUsageModel.EnergyUsageEntry.COLUMN_DATETIME + " <= " + to,
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
        euModels.add(new EnergyUsageModel(cursor));

        while (cursor.moveToNext())
            euModels.add(new EnergyUsageModel(cursor));

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

    public static void  insertDevice(ContentResolver resolver, DeviceModel model){
        resolver.insert(EnergyContract.Devices.CONTENT_URI, model.getContentValues());
    }

    public static void deleteAll(ContentResolver resolver){
        resolver.delete(EnergyContract.Devices.CONTENT_URI, null, null);
    }

    public static int addBatchEnergyModel(ContentResolver resolver, ContentValues[] values){
        //AsyncQueryHandler handler = new AsyncQueryHandler(resolver) {};

        Uri.Builder builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
        int n = resolver.bulkInsert(
                builder.build(),
                values
            );

        if(n != values.length)
            Log.e(TAG, "addBatchEnergyModel: Tried adding: " +values.length + ". Total added was: " + n);

        return n;
    }

}
