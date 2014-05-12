package com.sintef_energy.ubisolar.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by thb on 13.03.14.
 */
public class DeviceUsageList implements Parcelable
{
    private DeviceModel device;
    private ArrayList<EnergyUsageModel> usageList;
    private int totalUsage;
    private int percentage;

    public DeviceUsageList()
    {
        usageList = new ArrayList<>();
    }

    public DeviceUsageList(DeviceModel device) {
        this.device = device;
        usageList = new ArrayList<>();
    }

    public DeviceUsageList(Parcel in)
    {
        usageList = new ArrayList<>();
        readFromParcel(in);
    }

    public ArrayList<EnergyUsageModel> getUsage() {
        return usageList;
    }

    public void setUsage(ArrayList<EnergyUsageModel> usageList) {
        this.usageList = usageList;
    }

    public int size()
    {
        if(usageList == null)
            return 0;

        return usageList.size();
    }

    public EnergyUsageModel get(int index)
    {
        if(index >= usageList.size())
            return usageList.get(usageList.size() -1);
        return usageList.get(index);
    }

    public void add(EnergyUsageModel usage)
    {
        usageList.add(usage);
    }

    public int getTotalUsage()
    {
        return totalUsage;
    }


    public void calculateTotalUsage(String date, String format, int activeIndex)
    {
        boolean done = true;
        totalUsage = 0;

        //Poorly optimized code to get the complete usage for a timespace

        if(usageList.size() > activeIndex) {
            if (formatDate(usageList.get(activeIndex).toDate(), format).equals(date)) {
                totalUsage = (int) usageList.get(activeIndex).getPowerUsage();
                return;
            }
        }


        for(int i = usageList.size()-1; i >= 0; i--){
            if (formatDate(usageList.get(i).toDate(), format).equals(date))
            {
                totalUsage += usageList.get(i).getPowerUsage();
                done = false;
            }
            if(done && totalUsage > 0)
                break;
            done = true;
        }
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public DeviceModel getDevice()
    {
        return device;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(device, i);
        parcel.writeParcelableArray(usageList.toArray( new EnergyUsageModel[usageList.size()]), i);
        parcel.writeInt(totalUsage);
        parcel.writeInt(percentage);
    }

    private void readFromParcel(Parcel in) {
        device = in.readParcelable(Device.class.getClassLoader());
        EnergyUsageModel[] u = (EnergyUsageModel[]) in.readParcelableArray(EnergyUsageModel.class.getClassLoader());

        for(EnergyUsageModel um : u)
            usageList.add(um);

        totalUsage = in.readInt();
        percentage = in.readInt();
    }

    private String formatDate(Date date, String format)
    {
        SimpleDateFormat formatter = new SimpleDateFormat (format);
        if(date != null)
            return formatter.format(date);
        else
            return null;
    }
}