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
package com.sintef_energy.ubisolar.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

    public DeviceUsageList(DeviceModel device) {
        this.device = device;
        usageList = new ArrayList<>();
    }

    public DeviceUsageList(Parcel in) {
        usageList = new ArrayList<>();
        readFromParcel(in);
    }

    public ArrayList<EnergyUsageModel> getUsage() {
        return usageList;
    }

    public void setUsage(ArrayList<EnergyUsageModel> usageList) {
        this.usageList = usageList;
    }

    /**
     * The usageLists size
     * @return
     */
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


    /**
     * Calculates the total usage for the given resolution
     * @param date The date of the usage
     * @param format The format to check for
     * @param activeIndex The selected index
     */
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

    public static final Parcelable.Creator<DeviceUsageList> CREATOR = new Parcelable.Creator<DeviceUsageList>() {
        public DeviceUsageList createFromParcel(Parcel in) {
            return new DeviceUsageList(in);
        }

        public DeviceUsageList[] newArray(int size) {
            return new DeviceUsageList[size];
        }
    };


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

        usageList.addAll(Arrays.asList(u));

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