package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.presenter.TipPresenter;
import com.sintef_energy.ubisolar.structs.Tip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by perok on 2/11/14.
 */
public class PowerSavingFragment extends DefaultTabFragment {
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    ListView tipsView;
    ArrayList<Tip> tips;
    public static PowerSavingFragment newInstance(int sectionNumber) {
        PowerSavingFragment fragment = new PowerSavingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PowerSavingFragment() {
    }

    /**
     * The first call to a created fragment
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Callback to activity
        ((DrawerActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_power_saving, container, false);
        ArrayAdapter<Tip> tipAdapter = new ArrayAdapter<Tip>(getActivity(), android.R.layout.simple_list_item_1, tips);
        tipsView = (ListView) rootView.findViewById(R.id.tipList);
        tips = new ArrayList<>();
        tipsView.setAdapter(tipAdapter);
        
        //Get all tips from server asynchronously
        ((DrawerActivity) getActivity()).getTipPresenter().getAllTips(tipAdapter, tips);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            // Restore last state for checked position.
        }
    }

    /*End lifecycle*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
