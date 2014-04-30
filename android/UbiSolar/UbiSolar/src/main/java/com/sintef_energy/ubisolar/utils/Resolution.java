package com.sintef_energy.ubisolar.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by thb on 30.04.14.
 */
public class Resolution {

    public static final int HOURS = 0;
    public static final int DAYS = 1;
    public static final int WEEKS = 2;
    public static final int MONTHS = 3;

    private int mode;

    //Line graph formats
    private String resolutionFormat;
    private String titleFormat;
    private String compareFormat;

    //Pie graph formats
    private String preLabel;
    private String pieFormat;

    public Resolution(int mode){
        this.mode = mode;
        setFormat(mode);
    }


    public void setFormat(int mode){
        this.mode = mode;
        switch (mode)
        {
            case HOURS:
                resolutionFormat = "HH";
                titleFormat = "EEEE dd/MM";
                compareFormat = "yyyyDDHH";

                preLabel = "KL ";
                pieFormat = "HH EEEE dd/MM";
                break;
            case DAYS:
                resolutionFormat = "dd";
                titleFormat = "MMMM";
                compareFormat = "yyyyDD";

                preLabel = "";
                pieFormat = "EEEE dd/MM";
                break;
            case WEEKS:
                resolutionFormat = "w";
                titleFormat = "MMMMM y";
                compareFormat = "yyyyw";

                preLabel = "Week ";
                pieFormat = "w y";
                break;
            case MONTHS:
                resolutionFormat = "MMMM";
                titleFormat = "y";
                compareFormat = "yyyyMM";

                preLabel = "";
                pieFormat = "MMMM";
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

    public String getPreLabel(){
        return preLabel;
    }

    public String getPieFormat(){
        return pieFormat;
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
            return (int) TimeUnit.MILLISECONDS.toHours(diff) + 1;
        else if(mode == DAYS)
            return (int) TimeUnit.MILLISECONDS.toDays(diff) + 1;
        else if(mode == WEEKS)
            return (int) TimeUnit.MILLISECONDS.toDays(diff) / 7 + 1;
        else if(mode == MONTHS)
            return (int) TimeUnit.MILLISECONDS.toDays(diff) / 30 + 1;
        else
            return -1;
    }

    public int getMode(){
        return mode;
    }
}
