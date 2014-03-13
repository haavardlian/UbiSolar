package com.sintef_energy.ubisolar.IView;

import com.sintef_energy.ubisolar.database.energy.DeviceModel;

/**
 * Created by pialindkjolen on 05.03.14.
 */
public interface IDeviceView {
    public void addDevice(DeviceModel model);
    public void deleteDevice(DeviceModel model);
    public void changeDevice(DeviceModel model, String name, String description);
}
