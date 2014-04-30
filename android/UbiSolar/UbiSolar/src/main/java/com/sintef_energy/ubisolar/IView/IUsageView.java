package com.sintef_energy.ubisolar.IView;

import android.app.LoaderManager;

import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.fragments.UsageFragment;
import com.sintef_energy.ubisolar.model.DeviceUsageList;

import java.util.ArrayList;

public interface IUsageView {
    public void addDeviceUsage(ArrayList<DeviceUsageList> usageList);
    public void clearDevices();

    public void setFormat(int mode);
    public int getResolution();

    public boolean[] getSelectedDialogItems();
    public void setSelectedDialogItems(boolean[] selectedDialogItems);

    public void setActiveIndex(int index);
    public int getActiveIndex();

    public boolean isLoaded();
    public void setDeviceSize(int size);
}