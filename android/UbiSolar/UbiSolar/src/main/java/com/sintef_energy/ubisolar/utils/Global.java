package com.sintef_energy.ubisolar.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Håvard on 10.02.14.
 */
public class Global {
    public static Boolean loggedIn = false;

    public static String URL = "188.226.188.11:1337";
    public static String BASE_URL = "http://" + URL;

    public static boolean DEVELOPER_MADE = false;

    /* Broadcasts and and data. Broadcasts are set from string.xml */
    public static String BROADCAST_NAV_DRAWER = "";
    public static final String DATA_B_NAV_DRAWER_USAGE = "DATA_B_NAV_DRAWER_USAGE";

    public static final String ACTION_LOGIN_FB = "ACTION_LOGIN_FB";
    public static final String DATA_LOGIN_ACCOUNT_TYPE = "DATA_LOGIN_ACCOUNT_TYPE";
    public static final String DATA_LOGIN_AUTH_TOKEN_TYPE = "DATA_LOGIN_AUTH_TOKEN_TYPE";
    public static final String DATA_AUTH_TOKEN = "DATA_AUTH_TOKEN";
    public static final String DATA_EXPIRATION_DATE = "DATA_EXPIRATION_DATE";
    public static final String DATA_FB_UID = "DATA_DB_UID";


    //Facebook permissions
    public static List<String> FACEBOOK_READ_PERMISSIONS;
    static {
        FACEBOOK_READ_PERMISSIONS = new ArrayList<>();
        FACEBOOK_READ_PERMISSIONS.add("user_birthday");
        FACEBOOK_READ_PERMISSIONS.add("user_location");
        FACEBOOK_READ_PERMISSIONS.add("email");
    }

    public static List<String> FACEBOOK_PUBLISH_PERMISSIONS;
    static {
        FACEBOOK_READ_PERMISSIONS = new ArrayList<>();
        FACEBOOK_READ_PERMISSIONS.add("publish_stream");
    }

    public static List<String> FACEBOOK_PERMISSIONS = new ArrayList<>();
    static{
        FACEBOOK_PERMISSIONS.addAll(FACEBOOK_READ_PERMISSIONS);
        FACEBOOK_PERMISSIONS.addAll(FACEBOOK_PUBLISH_PERMISSIONS);
    }
}
