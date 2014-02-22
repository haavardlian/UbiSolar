package com.sintef_energy.ubisolar.database.energy;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by perok on 2/11/14.
 *
 * Use this class to write all code to access data.
 *
 */
public class EnergyDataSource {

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

     public static ArrayList<EnergyUsageModel> getEnergyModels(ContentResolver resolver, long from, long to){
        ArrayList<EnergyUsageModel> euModels = new ArrayList<>();

        Uri.Builder builder = EnergyContract.Energy.CONTENT_URI.buildUpon();
        //ContentUris.appendId(builder, id);
        Cursor cursor = resolver.query(
                builder.build(),
                EnergyContract.Energy.PROJECTION_ALL,
                EnergyUsageModel.EnergyUsageEntry.COLUMN_DATESTART + " >= " + from +
                    " AND " + EnergyUsageModel.EnergyUsageEntry.COLUMN_DATEEND + " <= " + to,
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
}
