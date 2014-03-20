package com.sintef_energy.ubisolar.fragments;

import android.app.Fragment;
import android.os.Bundle;

import com.sintef_energy.ubisolar.activities.DrawerActivity;

/**
 * Created by perok on 20.03.14.
 */
public abstract class DefaultTabFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    protected static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public void onResume(){
        super.onResume();

        ((DrawerActivity) getActivity()).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        ((DrawerActivity) getActivity()).restoreActionBar();
        //getActivity().getActionBar().setTitle();
    }

}
