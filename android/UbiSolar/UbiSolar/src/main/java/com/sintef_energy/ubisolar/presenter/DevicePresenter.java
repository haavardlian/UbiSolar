package com.sintef_energy.ubisolar.presenter;

import android.content.ContentResolver;
import android.util.Log;

import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.IView.IDeviceView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by pialindkjolen on 05.03.14.
 */
public class DevicePresenter {

    private static final String TAG = DevicePresenter.class.getName();

    /* The listeners */
    ArrayList<IDeviceView> dmListeners;

    public DevicePresenter(){}

    /* Listeners */
    public void registerListener(IDeviceView view){
        this.dmListeners.add(view);
    }

    public void unregisterListener(IDeviceView view){
        this.dmListeners.remove(view);
    }

    public void addDevice(DeviceModel device, ContentResolver contentResolver){
        device.updateLastUpdate();
        EnergyDataSource.insertDevice(contentResolver, device);
        //this.dmModels.add(device);
    }

    public void editDevice(ContentResolver contentResolver, DeviceModel dm) {
        //TODO: Add support for actually checking that the update went fine.
        dm.updateLastUpdate();
        EnergyDataSource.editDevice(contentResolver, dm);
    }
}
