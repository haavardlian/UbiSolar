package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.adapter.ComparisonAdapter;
import com.sintef_energy.ubisolar.adapter.NewsFeedAdapter;
import com.sintef_energy.ubisolar.model.NewsFeed;

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
    private ArrayList<NewsFeed> newsFeed;
    private NewsFeedAdapter newsFeedAdapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HomeFragment newInstance(int sectionNumber, NewsFeedAdapter newsFeedAdapter) {
        HomeFragment fragment = new HomeFragment(newsFeedAdapter);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
    }

    public HomeFragment(NewsFeedAdapter newsFeedAdapter) {
        this.newsFeedAdapter= newsFeedAdapter;
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

        newsFeed = new ArrayList<NewsFeed>();
        NewsFeedAdapter newsFeedAdapter = new NewsFeedAdapter(getActivity(),R.layout.fragment_home_row, newsFeed);
        final ListView friendsList = (ListView) view.findViewById(R.id.news_feed_list);
        friendsList.setAdapter(newsFeedAdapter);

        newsFeed.add(new NewsFeed("Pia and 5 others started with Wattitude!"));
        newsFeed.add(new NewsFeed("Harald shared a tip!"));
        newsFeed.add(new NewsFeed("Kåre also started with Wattitude!"));
        newsFeed.add(new NewsFeed("Pia shared her consumption"));
        newsFeed.add(new NewsFeed("Beate shared her consumption"));
        newsFeed.add(new NewsFeed("Håkon shared a tip!"));
        newsFeed.add(new NewsFeed("Per and 1 other started with Wattitude!"));
        newsFeed.add(new NewsFeed("Beate shared a tip!"));
        newsFeed.add(new NewsFeed("Lars also started with Wattitude!"));
        newsFeed.add(new NewsFeed("Per shared his consumption"));
        newsFeed.add(new NewsFeed("Kristin shared her consumption"));
        newsFeed.add(new NewsFeed("Håvard shared a tip!"));

        newsFeedAdapter.notifyDataSetChanged();

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