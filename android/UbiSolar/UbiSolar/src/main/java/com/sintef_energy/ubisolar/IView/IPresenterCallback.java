package com.sintef_energy.ubisolar.IView;

import com.sintef_energy.ubisolar.presenter.DevicePresenter;
import com.sintef_energy.ubisolar.presenter.ResidencePresenter;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;
import com.sintef_energy.ubisolar.presenter.UserPresenter;

/**
 * Created by perok on 12.03.14.
 */
public interface IPresenterCallback {

    public TotalEnergyPresenter getmTotalEnergyPresenter();
    public DevicePresenter getDevicePresenter();
    public ResidencePresenter getResidencePresenter();
    public UserPresenter getUserPresenter();
}
