package com.sintef_energy.ubisolar.IView;

import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.structs.DeviceUsageList;

import java.util.ArrayList;

public interface ITotalEnergyView {
    @Deprecated
    public void newData(EnergyUsageModel euModel);
    public void addDeviceUsage(ArrayList<DeviceUsageList> usageList);
    public void clearDevices();
}