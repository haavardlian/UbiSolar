package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.widget.ProfilePictureView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.adapter.ResidenceListAdapter;
import com.sintef_energy.ubisolar.model.Residence;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by perok on 2/11/14.
 *
 * UI design based on: https://github.com/gabrielemariotti/cardslib
 */
public class ProfileFragment extends DefaultTabFragment  {

    public static final String TAG = ProfileFragment.class.getName();
    private View mRootView;
    private ExpandableListView expListView;
    private ArrayList<Residence> residences;
    PreferencesManager prefs;

    private TextView name, location, age, country;
    private ProfilePictureView profilePicture;
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
        prefs = PreferencesManager.getInstance();

        //Dummy creation to be replaced when facebook login is 100%
        setDummyPrefs();

        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);
        setupList();

        name = (TextView) mRootView.findViewById(R.id.profile_name);
        name.setText(prefs.getFacebookName());
        location = (TextView) mRootView.findViewById(R.id.profile_location);
        location.setText(prefs.getFacebookLocation());
        age = (TextView) mRootView.findViewById(R.id.profile_age);
        age.setText(prefs.getFacebookAge());
        country = (TextView) mRootView.findViewById(R.id.profile_country);
        country.setText(prefs.getFacebookCountry());
        profilePicture = (ProfilePictureView) mRootView.findViewById(R.id.profile_profile_picture);
        profilePicture.setProfileId(prefs.getKeyFacebookUid());
        profilePicture.setPresetSize(ProfilePictureView.LARGE);

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
        expListView.setOnChildClickListener(expListAdapter);
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

    private void setDummyPrefs() {
        prefs.setFacebookName("Lars Erik Græsdal-Knutrud");
        prefs.setFacebookLocation("Trondheim");
        prefs.setFacebookAge("09/01/1991");
        prefs.setFacebookCountry("Norway");
        prefs.setKeyFacebookUid("736583709");
    }


}
