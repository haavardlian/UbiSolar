package com.sintef_energy.ubisolar.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by perok on 13.04.14.
 */
public class Utils {

    public static void makeShortToast(Context c, String text){
        Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
    }
}
