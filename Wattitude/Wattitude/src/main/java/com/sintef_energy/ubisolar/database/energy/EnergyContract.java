/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.sintef_energy.ubisolar.database.energy;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by perok on 2/11/14.
 *
 * For all CRUD data manipulation through ContentProvider.
 *
 * Note: By the delete command, data is not deleted. It's delete bit is set to true.
 * This will make the data not visible on queries.
 * To actually delete the data, append the extra DELETE string URI.
 *
 */
public class EnergyContract {
    /**
     * The authority of the lentitems provider.
     */
    public static final String AUTHORITY = "com.sintef_energy.ubisolar.provider.energy";

    /**
     * The content URI for the top-level item authority.
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * A selection clause for ID based queries.
     */
    public static final String SELECTION_ID_BASED = BaseColumns._ID + " = ? ";

    /**
     * The URI for deleting data.
     */
    public static final String DELETE = "delete";

    /**
     * Constants for the Items table of the Devices provider.
     */
    public static final class Devices implements DeviceModel.DeviceEntry {
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(EnergyContract.CONTENT_URI, DeviceModel.DeviceEntry.TABLE_NAME);
        /**
         * The mime type of a directory of items.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.sintef_energy.ubisolar.db.DeviceModel_items";
        /**
         * The mime type of a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.sintef_energy.ubisolar.db.DeviceModel_items";
        /**
         * A projection of all columns in the items table.
         */
        public static final String[] PROJECTION_ALL = DeviceModel.projection;
        /**
         * The default sort order for queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = BaseColumns._ID + " ASC";
    }

    /**
     * Constants for the Items table of the Devices provider.
     */
    public static final class Energy implements EnergyUsageModel.EnergyUsageEntry {
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(EnergyContract.CONTENT_URI, EnergyUsageModel.EnergyUsageEntry.TABLE_NAME);
        /**
         * The mime type of a directory of items.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.sintef_energy.ubisolar.db.EnergyUsageModel_items";
        /**
         * The mime type of a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.sintef_energy.ubisolar.db.EnergyUsageModel_items";
        /**
         * A projection of all columns in the items table.
         */
        public static final String[] PROJECTION_ALL = EnergyUsageModel.projection;
        /**
         * The default sort order for queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = BaseColumns._ID + " ASC";

        /**
         * Used with Energy table. Add /*value* to URI to group on that date type
         */
        public static interface Date {
            public static final String Day = "date";
            public static final String Week = "week";
            public static final String Month = "month";
            public static final String Year = "year";
        }
    }

    public static final class Users implements UserModel.UserEntry {
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(EnergyContract.CONTENT_URI, UserModel.UserEntry.TABLE_NAME);
        /**
         * The mime type of a directory of items.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.sintef_energy.ubisolar.db.UserModel_items";
        /**
         * The mime type of a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.sintef_energy.ubisolar.db.UserModel_items";
        /**
         * A projection of all columns in the items table.
         */
        public static final String[] PROJECTION_ALL = UserModel.projection;
        /**
         * The default sort order for queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = BaseColumns._ID + " ASC";
    }
}
