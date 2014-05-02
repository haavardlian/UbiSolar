package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.widget.ProfilePictureView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.SimilarAdapter;
import com.sintef_energy.ubisolar.model.ResidenceAttributes;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;

import java.util.ArrayList;

/**
 * Created by baier on 4/1/14.
 */
public class CompareSimilarFragment extends Fragment {

    /* The fragment argument representing the section number for this
     * fragment.
     */
    public static final String TAG = CompareSimilarFragment.class.getName();

    //private ComparisonAdapter compAdapter;
    private SimilarAdapter simAdapter;

    private View view;
    private static final String ARG_POSITION = "position";
    private ProfilePictureView profilePicture;

    public CompareSimilarFragment(SimilarAdapter simAdapter) {
        this.simAdapter = simAdapter;
    }

    public CompareSimilarFragment() {

    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CompareSimilarFragment newInstance(int position, SimilarAdapter simAdapter) {
        CompareSimilarFragment fragment = new CompareSimilarFragment(simAdapter);
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    public static CompareSimilarFragment newInstance(int position) {
        CompareSimilarFragment fragment = new CompareSimilarFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * The first call to a created fragment
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_similar_compare, container, false);
        profilePicture = (ProfilePictureView) view.findViewById(R.id.userProfilePic);
        profilePicture.setProfileId(PreferencesManager.getInstance().getKeyFacebookUid());
        profilePicture.setPresetSize(ProfilePictureView.LARGE);

        return view;
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
    public void onDestroy() {
        super.onDestroy();
    }

}