/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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

    /** Read permissions */
    public static List<String> FACEBOOK_READ_PERMISSIONS;
    /** Publish permsissions*/
    public static List<String> FACEBOOK_PUBLISH_PERMISSIONS;
    /** A collection of read and publish permissions */
    public static List<String> FACEBOOK_PERMISSIONS;

    static {
        FACEBOOK_READ_PERMISSIONS = new ArrayList<>();
        FACEBOOK_READ_PERMISSIONS.add("user_birthday");
        FACEBOOK_READ_PERMISSIONS.add("user_location");
        FACEBOOK_READ_PERMISSIONS.add("email");

        FACEBOOK_PUBLISH_PERMISSIONS = new ArrayList<>();
        FACEBOOK_PUBLISH_PERMISSIONS.add("publish_stream");

        FACEBOOK_PERMISSIONS = new ArrayList<>();
        FACEBOOK_PERMISSIONS.addAll(FACEBOOK_READ_PERMISSIONS);
        FACEBOOK_PERMISSIONS.addAll(FACEBOOK_PUBLISH_PERMISSIONS);
    }
}
