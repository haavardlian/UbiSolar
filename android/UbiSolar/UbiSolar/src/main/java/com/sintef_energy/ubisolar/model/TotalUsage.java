package com.sintef_energy.ubisolar.model;

import java.util.Date;

/**
 * Created by thb on 19.02.14.
 */
public class TotalUsage
{
    private double power_usage;
    private Date datetime;
    private long user_id;

    private TotalUsage()
    {

    }

    public TotalUsage(int user_id, long datetime, double power_usage) {
        this(user_id, new Date(datetime), power_usage);
    }

    public TotalUsage(long user_id, Date datetime, double power_usage) {
        this.power_usage = power_usage;
        this.datetime = datetime;
        this.user_id = user_id;
    }


    public double getPower_usage() {
        return power_usage;
    }

    public Date getDatetime() {
        return datetime;
    }

    public long getUser_id() {
        return user_id;
    }

//    public String getFormatedDate()
//    {
//        System.out.println(datetime);
//        SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
//        return ft.format(datetime);
//    }
}
