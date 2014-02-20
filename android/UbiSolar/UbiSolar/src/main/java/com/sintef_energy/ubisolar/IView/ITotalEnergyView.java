package com.sintef_energy.ubisolar.IView;

import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;

/**
 * Created by perok on 2/19/14.
 */
public interface ITotalEnergyView {
    public void dataRefresh();
    public void newData(EnergyUsageModel euModel);
}
