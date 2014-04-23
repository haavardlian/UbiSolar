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

    //TODO arrange the data to be sent to the view, react to changes in the view

    private static final String TAG = DevicePresenter.class.getName();

    /* The Models*/
    ArrayList<DeviceModel> dmModels;
    /* The listeners */
    ArrayList<IDeviceView> dmListeners;

    public DevicePresenter(){
      dmModels = new ArrayList<DeviceModel>();
      dmListeners = new ArrayList<IDeviceView>();
    };

    /* Listeners */
    public void registerListener(IDeviceView view){
        this.dmListeners.add(view);
    }

    public void unregisterListener(IDeviceView view){
        this.dmListeners.remove(view);
    }

    public void addDevice(DeviceModel device, ContentResolver contentResolver){
        EnergyDataSource.insertDevice(contentResolver, device);
        //this.dmModels.add(device);
    }

    public ArrayList<DeviceModel> getDeviceModels(ContentResolver contentResolver){
        dmModels = EnergyDataSource.getAllDeviceModels(contentResolver);
        return dmModels;
    }

    public void deleteDevices(ContentResolver contentResolver){
        EnergyDataSource.deleteAll(contentResolver);
    }
}
