package com.sintef_energy.ubisolar.database.energy;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by perok on 2/11/14.
 *
 * Use this class to write all code to access data.
 *
 */
public class EnergyDataSource {
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
}
