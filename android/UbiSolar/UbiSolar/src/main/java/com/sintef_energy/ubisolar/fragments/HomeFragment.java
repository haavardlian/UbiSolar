package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.facebook.Session;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.adapter.WallAdapter;
import com.sintef_energy.ubisolar.model.WallPost;
import com.sintef_energy.ubisolar.presenter.RequestManager;

import java.util.ArrayList;

/**
 * Created by perok on 2/11/14.
 */
public class HomeFragment extends DefaultTabFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String TAG = HomeFragment.class.getName();

    private View view;
    private ArrayList<WallPost> wallFeed;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
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
        view = inflater.inflate(R.layout.fragment_home, container, false);

        wallFeed = new ArrayList<>();
        WallAdapter wallAdapter = new WallAdapter(getActivity(),R.layout.wall_item, wallFeed);
        final ListView friendsList = (ListView) view.findViewById(R.id.news_feed_list);
        friendsList.setAdapter(wallAdapter);

        if(Session.getActiveSession().isOpened())
            RequestManager.getInstance().doFacebookRequest().populateWall(wallAdapter, this);

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
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}