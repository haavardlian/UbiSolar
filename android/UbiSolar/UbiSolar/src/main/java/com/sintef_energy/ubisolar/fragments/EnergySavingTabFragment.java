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
import com.sintef_energy.ubisolar.adapter.YourAdapter;
import com.sintef_energy.ubisolar.dialogs.AddTipDialog;
import com.sintef_energy.ubisolar.model.Tip;

import java.util.ArrayList;

/**
 * Created by perok on 21.03.14.
 */
public class EnergySavingTabFragment extends DefaultTabFragment {

    private static final String TAG = EnergySavingTabFragment.class.getName();

    private View mRoot;
    private TipsPagerAdapter mPagerAdapter;

    public static EnergySavingTabFragment newInstance(int sectionNumber) {
        EnergySavingTabFragment fragment = new EnergySavingTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
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

        if(mPagerAdapter == null)
            mPagerAdapter = new TipsPagerAdapter(getFragmentManager());

        // Initialize the ViewPager and set an adapter
        final ViewPager pager = (ViewPager) mRoot.findViewById(R.id.fragment_energy_saving_pager);
        pager.setAdapter(mPagerAdapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) mRoot.findViewById(R.id.fragment_energy_saving_tabs);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position) {
                    case 0:
                        ((TipsPagerAdapter)pager.getAdapter()).getTipsFragment().getAdapter().notifyDataSetChanged();
                        break;
                    case 1:
                        ((TipsPagerAdapter)pager.getAdapter()).getYourFragment().getAdapter().notifyDataSetChanged();
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_tip, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        /* Moved to deviceTab */
            case R.id.menu_add_tip:
                AddTipDialog dialog = new AddTipDialog();
                dialog.setTargetFragment(this, 0);
                dialog.show(getFragmentManager(), "addTipDialog");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public TipsPagerAdapter getAdapter() { return mPagerAdapter; }

    public class TipsPagerAdapter extends FragmentStatePagerAdapter {

        private String[] titles;
        private YourAdapter yourAdapter;
        private TipsFragment tipsFragment;
        private YourFragment yourFragment;
        public TipsPagerAdapter(FragmentManager fm) {
            super(fm);

            titles = getResources().getStringArray(R.array.fragment_energy_saving_tabs);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        public TipsFragment getTipsFragment() {
            return tipsFragment;
        }

        public YourFragment getYourFragment() {
            return yourFragment;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    tipsFragment = TipsFragment.newInstance(0);
                    tipsFragment.setTargetFragment(EnergySavingTabFragment.this, 0);
                    return tipsFragment;
                case 1:
                    yourFragment = YourFragment.newInstance(0);
                    return yourFragment;
                default:
                    return null;
            }
        }

    }
}
