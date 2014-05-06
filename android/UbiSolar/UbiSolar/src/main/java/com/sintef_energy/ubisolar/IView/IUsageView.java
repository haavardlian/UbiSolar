package com.sintef_energy.ubisolar.IView;

import android.graphics.Bitmap;

import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.model.DeviceUsageList;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface IUsageView {
    public void setResolution(int mode);
    public int getResolution();

    public boolean[] getSelectedDialogItems();
    public void setSelectedDialogItems(boolean[] selectedDialogItems);

    public void setActiveIndex(int index);
    public int getActiveIndex();

    /** Sets the progress view to load or not. */
    public void setDataLoading(boolean state);

    public void setDevices(LinkedHashMap<Long, DeviceModel> devices);

    public Bitmap createImage();

    public void pullData();
}