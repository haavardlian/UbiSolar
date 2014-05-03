package com.sintef_energy.ubisolar.fragments;

/**
 * Created by baier on 5/1/14.
 */

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.widget.ProfilePictureView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.SimilarAdapter;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;


public class CompareFriendsFragment extends Fragment {

    /* The fragment argument representing the section number for this
     * fragment.
     */
    public static final String TAG = CompareFriendsFragment.class.getName();

    private SimilarAdapter simAdapter;

    private View view;
    private static final String ARG_POSITION = "position";
    private ProfilePictureView profilePicture;
    private ProfilePictureView friendPicture;


    public CompareFriendsFragment(SimilarAdapter simAdapter) {
        this.simAdapter = simAdapter;
    }

    public CompareFriendsFragment() {

    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CompareFriendsFragment newInstance(int position, SimilarAdapter simAdapter) {
        CompareFriendsFragment fragment = new CompareFriendsFragment(simAdapter);
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    public static CompareFriendsFragment newInstance(int position) {
        CompareFriendsFragment fragment = new CompareFriendsFragment();
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
        view = inflater.inflate(R.layout.fragment_friends_compare, container, false);
        profilePicture = (ProfilePictureView) view.findViewById(R.id.userProfilePic);
        profilePicture.setProfileId(PreferencesManager.getInstance().getKeyFacebookUid());
        profilePicture.setPresetSize(ProfilePictureView.LARGE);

        friendPicture = (ProfilePictureView) view.findViewById(R.id.friendProfilePic);
        friendPicture.setProfileId("");
        friendPicture.setPresetSize(ProfilePictureView.LARGE);

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