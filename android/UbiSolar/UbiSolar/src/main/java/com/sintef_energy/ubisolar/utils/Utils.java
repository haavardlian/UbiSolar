package com.sintef_energy.ubisolar.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.widget.Toast;

/**
 * Created by perok on 13.04.14.
 */
public class Utils {

    public static void makeShortToast(Context c, String text){
        Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper class to check if app has internet connection.
     * @param context
     * @return
     */
    public static boolean isNetworkOn(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }



    /**
     * Debug with strict mode
     */
    public static void developerMode(ContentResolver cr, boolean devMode, boolean testData){
        if(testData) {
            TestdataHelper.clearDatabase(cr);
//            //Populate the database if it's empty
//            if (EnergyDataSource.getEnergyModelSize(getContentResolver()) == 0) {
//                Log.v(TAG, "Developer mode: Database empty. Populating it.");
                TestdataHelper.createDevices(cr);
//                //            createEnergyUsage();
//            }
        }

        if(devMode){
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder()
                            .detectDiskReads()
                            .detectDiskWrites()
                            .detectNetwork()
                            .penaltyLog()
                            .build());

            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()
            .penaltyDeath()
            .build());
        }
    }


}
