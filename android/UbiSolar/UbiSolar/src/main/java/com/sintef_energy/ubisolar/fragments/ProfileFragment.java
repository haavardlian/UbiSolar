package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.Session;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.adapter.ResidenceListAdapter;
import com.sintef_energy.ubisolar.model.Residence;

import java.util.ArrayList;

/**
 * Created by perok on 2/11/14.
 *
 * UI design based on: https://github.com/gabrielemariotti/cardslib
 */
public class ProfileFragment extends DefaultTabFragment {

    public static final String TAG = DeviceFragment.class.getName();
    private View mRootView;
    private ExpandableListView expListView;
    private ArrayList<Residence> residences;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
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

        //return super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        setupList();
        Session.getActiveSession();
        return mRootView;
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

    private void setupList()
    {
        createGroupList();

        expListView = (ExpandableListView) mRootView.findViewById(R.id.residencesListView);
        final ResidenceListAdapter expListAdapter = new ResidenceListAdapter(getActivity(), residences);
        setGroupIndicatorToRight();
        expListView.setAdapter(expListAdapter);
    }

    private void createGroupList() {
        residences = new ArrayList<Residence>();
        residences.add(new Residence("Huset", "Stadsing Dahls gate", 6, 140, 7015,'A'));
        residences.add(new Residence("Hytta", "På fjellet", 2, 40, 4903,'G'));
        residences.add(new Residence("Kontoret","NTNU", 1, 15, 7018, 'B'));
        residences.add(new Residence("Spaniahuset", "Barcelona", 3, 80, 14390, 'D'));

    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

}
