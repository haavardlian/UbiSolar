package com.sintef_energy.ubisolar.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.astuetz.PagerSlidingTabStrip;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.adapter.YourAdapter;
import com.sintef_energy.ubisolar.model.Tip;

import java.util.ArrayList;

/**
 * Created by perok on 21.03.14.
 */
public class EnergySavingTabFragment extends DefaultTabFragment {

    private static final String TAG = EnergySavingTabFragment.class.getName();
    public static final String TAB_WORDS = "tips";
    public static final String TAB_NUMBERS = "your";

    private View mRoot;
    private TabHost mTabHost;
    private int mCurrentTab;
    private YourAdapter yourAdapter;

    public static EnergySavingTabFragment newInstance(int sectionNumber) {
        EnergySavingTabFragment fragment = new EnergySavingTabFragment();
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
        mRoot = inflater.inflate(R.layout.fragment_energy_saving_tab, container, false);
        //mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
        yourAdapter = new YourAdapter(getActivity(), R.layout.fragment_your_row, new ArrayList<Tip>());
        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) mRoot.findViewById(R.id.fragment_energy_saving_pager);
        pager.setAdapter(new MyPagerAdapter(getFragmentManager(), yourAdapter));
        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) mRoot.findViewById(R.id.fragment_energy_saving_tabs);
        tabs.setViewPager(pager);


        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        //mTabHost.setOnTabChangedListener(this);
        //mTabHost.setCurrentTab(mCurrentTab);
        // manually start loading stuff in the first tab
        //updateTab(TAB_WORDS, R.id.fragment_energy_saving_tab_tips);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = { "Tips", "Your tips"};
        private YourAdapter yourAdapter;
        public MyPagerAdapter(FragmentManager fm, YourAdapter yourAdapter) {
            super(fm);
            this.yourAdapter = yourAdapter;
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
                    return TipsFragment.newInstance(0, yourAdapter);
                case 1:
                    return YourFragment.newInstance(1, yourAdapter);
                default:
                    return null;
            }
        }

    }
}
