package com.sintef_energy.ubisolar.presenter;

import android.content.ContentResolver;

import com.sintef_energy.ubisolar.IView.ITotalEnergyView;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;

import java.util.ArrayList;

/**
 * Created by perok on 2/19/14.
 */
public class TotalEnergyPresenter {

    ArrayList<EnergyUsageModel> euModels;

    ArrayList<ITotalEnergyView> listeners;

    public TotalEnergyPresenter(){
        listeners = new ArrayList<>();
        euModels = new ArrayList<>();
    }

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
        euModels = EnergyDataSource.getEnergyModels(resolver, from, to);
    }

    public ArrayList<EnergyUsageModel> getEnergyData(){
        return euModels;
    }


    public void addEnergyData(EnergyUsageModel euModel){

        //TODO: Add data to DB
        //int status = EnergyDataSource

        euModels.add(euModel);

        for(ITotalEnergyView view : listeners)
            view.newData(euModel);
    }
}
