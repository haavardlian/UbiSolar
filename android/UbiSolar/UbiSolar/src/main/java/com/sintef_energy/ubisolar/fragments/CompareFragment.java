package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.adapter.FriendAdapter;
import com.sintef_energy.ubisolar.adapter.SimilarAdapter;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.dialogs.CompareSettingsDialog;
import com.sintef_energy.ubisolar.dialogs.SelectDevicesDialog;
import com.sintef_energy.ubisolar.dialogs.ShareDialog;
import com.sintef_energy.ubisolar.model.User;

import java.util.ArrayList;

/**
 * Created by perok on 21.03.14.
 */
public class CompareFragment extends DefaultTabFragment {

    private static final String TAG = CompareFragment.class.getName();

    private View mRoot;
    private FriendAdapter friendAdapter;
    private SimilarAdapter simAdapter;

    public static CompareFragment newInstance(int sectionNumber) {
        CompareFragment fragment = new CompareFragment();
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
        mRoot = inflater.inflate(R.layout.fragment_social_tab, container, false);
        //mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
        friendAdapter = new FriendAdapter(getActivity(), R.layout.fragment_social_row, new ArrayList<User>());
        simAdapter = new SimilarAdapter();
        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) mRoot.findViewById(R.id.fragment_social_pager);
        pager.setAdapter(new MyPagerAdapter(getFragmentManager(), friendAdapter, simAdapter));
        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) mRoot.findViewById(R.id.fragment_social_tabs);
        tabs.setViewPager(pager);

        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        private final String[] TITLES = { "Friends", "Similar profiles"};
        private FriendAdapter friendAdapter;
        private SimilarAdapter simAdapter;
        public MyPagerAdapter(FragmentManager fm, FriendAdapter friendAdapter, SimilarAdapter simAdapter) {
            super(fm);
            this.friendAdapter = friendAdapter;
            this.simAdapter = simAdapter;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return CompareFriendsListFragment.newInstance(0, friendAdapter);
                case 1:
                    return CompareSimilarFragment.newInstance(1, simAdapter);
                default:
                    return null;
            }
        }


    }


}
