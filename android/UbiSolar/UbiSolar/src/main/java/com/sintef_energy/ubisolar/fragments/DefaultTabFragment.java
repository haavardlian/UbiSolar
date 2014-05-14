package com.sintef_energy.ubisolar.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

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
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume(){
        super.onResume();

        ((DrawerActivity) getActivity()).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        ((DrawerActivity) getActivity()).restoreActionBar();
    }

    /**
     * A grand and ugly hack. UsageFragment uses a ViewPager, and it's inner Fragment uses the ActionBar
     * menu. This causes an issue when changing tabs that the ViewPager does not remove the .
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
