package com.sintef_energy.ubisolar.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

}
