package com.sintef_energy.ubisolar.database.energy;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.CalendarContract;

/**
 * Created by perok on 2/11/14.
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
     * Constants for the Items table of the Devices provider.
     */
        public static final class Devices implements DeviceModel.DeviceEntry {
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI =  Uri.withAppendedPath(EnergyContract.CONTENT_URI, DeviceModel.DeviceEntry.TABLE_NAME);
        /**
         * The mime type of a directory of items.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.sintef_energy.ubisolar.db.DeviceModel_items";
        /**
         * The mime type of a single item.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.sintef_energy.ubisolar.db.DeviceModel_items";
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
        public static final Uri CONTENT_URI =  Uri.withAppendedPath(EnergyContract.CONTENT_URI, EnergyUsageModel.EnergyUsageEntry.TABLE_NAME);
        /**
         * The mime type of a directory of items.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.sintef_energy.ubisolar.db.EnergyUsageModel_items";
        /**
         * The mime type of a single item.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.sintef_energy.ubisolar.db.EnergyUsageModel_items";
        /**
         * A projection of all columns in the items table.
         */
        public static final String[] PROJECTION_ALL = EnergyUsageModel.projection;
        /**
         * The default sort order for queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = BaseColumns._ID + " ASC";
    }
}
