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

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Resolution {

    public static final int HOURS = 0;
    public static final int DAYS = 1;
    public static final int WEEKS = 2;
    public static final int MONTHS = 3;
    public static final int YEARS = 4;

    private int mode;

    //Line graph formats
    private String resolutionFormat;
    private String titleFormat;
    private String compareFormat;

    public Resolution(int mode){
        this.mode = mode;
        setFormat(mode);
    }


    public void setFormat(int mode){
        this.mode = mode;
        switch (mode){
            case HOURS:
                resolutionFormat = "HH";
                titleFormat = "EEEE dd/MM";
                compareFormat = "yyyyDDHH";

                break;
            case DAYS:
                resolutionFormat = "dd";
                titleFormat = "MMMM";
                compareFormat = "yyyyDD";

                break;
            case WEEKS:
                resolutionFormat = "w";
                titleFormat = "MMMMM y";
                compareFormat = "yw";

                break;
            case MONTHS:
                resolutionFormat = "MMMM";
                titleFormat = "y";
                compareFormat = "yyyyMM";

                break;
            case YEARS:
                resolutionFormat = "yyyy";
                titleFormat = "";
                compareFormat = "yyyy";

                break;
        }
    }

    public String getResolutionFormat() {
        return resolutionFormat;
    }

    public String getTitleFormat() {
        return titleFormat;
    }

    public String getCompareFormat() {
        return compareFormat;
    }


    public void getNextPoint(Calendar cal){
        if(mode == HOURS)
            cal.add(Calendar.HOUR_OF_DAY, 1);
        else if(mode == DAYS)
            cal.add(Calendar.DAY_OF_MONTH, 1);
        else if(mode == WEEKS)
            cal.add(Calendar.WEEK_OF_YEAR, 1);
        else if(mode == MONTHS)
            cal.add(Calendar.MONTH, 1);
    }

    public int getTimeDiff(Date date1, Date date2){
        long diff = date2.getTime() - date1.getTime();

        if(mode == HOURS)
            return (int) Math.ceil((double)TimeUnit.MILLISECONDS.toHours(diff)) + 1;
        else if(mode == DAYS)
            return (int) Math.ceil((double)TimeUnit.MILLISECONDS.toDays(diff)) + 1;
        else if(mode == WEEKS)
            return (int) Math.ceil((double)TimeUnit.MILLISECONDS.toDays(diff) / 7) + 1;
        else if(mode == MONTHS)
            return (int) Math.ceil((double)TimeUnit.MILLISECONDS.toDays(diff) / 30) + 1;
        else
            return -1;
    }

    public int getMode(){
        return mode;
    }
}
