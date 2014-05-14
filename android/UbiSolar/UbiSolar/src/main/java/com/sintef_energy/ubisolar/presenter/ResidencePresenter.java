package com.sintef_energy.ubisolar.presenter;

import android.content.ContentResolver;
import android.util.Log;
import com.sintef_energy.ubisolar.IView.IResidenceView;
import com.sintef_energy.ubisolar.database.energy.ResidenceModel;

import java.util.ArrayList;

/**
 * Created by Lars Erik on 03.05.2014.
 */
public class ResidencePresenter {

    private static final String TAG = ResidencePresenter.class.getName();

    private ArrayList<IResidenceView> residenceModelListeners;

    public ResidencePresenter(){}

    //TODO: Implement storage

    /**
     * Currently only a stub method for adding residence.
     * @param residence
     * @param contentResolver
     */
    public void addResidence(ResidenceModel residence, ContentResolver contentResolver){
        Log.d(TAG,"added residence" + residence.getHouseName());

    }

    public void editResidence(ContentResolver contentResolver, ResidenceModel residence) {
    }
}
