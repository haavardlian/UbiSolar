
/*
* Copyright (C) 2008 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

/**
* package-level logging flag
*/

package com.sintef_energy.ubisolar.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
   public final static String LOGTAG = "Alarmz";

   /** This must be false for production.  If true, turns on logging,
    test code, etc. */
   static final boolean LOGV = true;

   public static void v(String LOG, String logMe) {
	   if(LOGV)
		   android.util.Log.v(LOG, /* SystemClock.uptimeMillis() + &quot; &quot; + */ logMe);
   }

   public static void d(String LOG, String logMe) {
	   if(LOGV)
		   android.util.Log.d(LOG, /* SystemClock.uptimeMillis() + &quot; &quot; + */ logMe);
   }

    public static void i(String LOG, String logMe) {
	   if(LOGV)
		   android.util.Log.i(LOG, logMe);
   }

   public static void e(String LOG, String logMe, Exception ex) {
	   if(LOGV)
		   android.util.Log.e(LOG, logMe, ex);
   }

   public static void e(String LOG, String logMe) {
        if(LOGV)
            android.util.Log.e(LOG, logMe);
   }
   public static void w(String LOG, String logMe) {
	   if(LOGV)
		   android.util.Log.w(LOG, logMe);
   }

   public static void wtf(String LOG, String logMe) {
	   if(LOGV)
		   android.util.Log.wtf(LOG, logMe);
   }

   public static String formatTime(long millis) {
	   return new SimpleDateFormat("HH:mm:ss.SSS/E").format(new Date(millis));
   }
}
