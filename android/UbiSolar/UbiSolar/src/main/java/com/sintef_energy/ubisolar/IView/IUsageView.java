package com.sintef_energy.ubisolar.IView;

import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.model.DeviceUsageList;

import java.util.ArrayList;

public interface IUsageView {
    @Deprecated
    public void newData(EnergyUsageModel euModel);
    public void addDeviceUsage(ArrayList<DeviceUsageList> usageList);
    public void clearDevices();
    public void redraw();

    public String[] getSelectedItems();
    public boolean[] getSelectedDialogItems();

    public void setSelectedItems(String[] selectedItems);
    public void setSelectedDialogItems(boolean[] selectedDialogItems);
}