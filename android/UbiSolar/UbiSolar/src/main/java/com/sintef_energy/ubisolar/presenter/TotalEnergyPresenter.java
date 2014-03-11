package com.sintef_energy.ubisolar.presenter;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.sintef_energy.ubisolar.IView.ITotalEnergyView;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by perok on 2/19/14.
 */
public class TotalEnergyPresenter {

    private static final String TAG = TotalEnergyPresenter.class.getName();

    /* The Models*/
    ArrayList<EnergyUsageModel> euModels;

    /* The listeners */
    ArrayList<ITotalEnergyView> listeners;

    public TotalEnergyPresenter(){
        listeners = new ArrayList<>();
        euModels = new ArrayList<>();
    }

    /* Listeners */
    public void registerListner(ITotalEnergyView view){
        listeners.add(view);
    }

    public void unregisterListener(ITotalEnergyView view){
        listeners.remove(view);
    }

    /**
     * Loads the data with ContentProvider.
     */
    public void loadEnergyData(ContentResolver resolver, long from , long to){
        ArrayList<EnergyUsageModel> tempEuModels = EnergyDataSource.getEnergyModels(resolver, from, to);

        if(tempEuModels != null){
            euModels = tempEuModels;
            Collections.sort(euModels);
        }
        else
            euModels = new ArrayList<>();

        for(ITotalEnergyView view : listeners)
            view.newData(null); //todo: handle correctly...
    }

    public ArrayList<EnergyUsageModel> getEnergyData(){
        return euModels;
    }


    /**
     * Assigns and ID to the EnergyUsageModel and adds it to the database and internal list.
     * Notifies all listeners.
     * @param euModel The EnergyUsageModel to add.
     */
    public void addEnergyData(ContentResolver resolver, EnergyUsageModel euModel){
        euModel.setId(System.currentTimeMillis());

        Uri uri = EnergyDataSource.addEnergyModel(resolver, euModel);

        Log.v(TAG, "addEnergyData: " + uri);

        euModels.add(euModel);

        Collections.sort(euModels);

        for(ITotalEnergyView view : listeners)
            view.newData(euModel);
    }
}
