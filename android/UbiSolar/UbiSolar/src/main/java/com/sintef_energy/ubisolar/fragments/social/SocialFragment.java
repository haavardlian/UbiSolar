package com.sintef_energy.ubisolar.fragments.social;

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
import com.sintef_energy.ubisolar.adapter.FriendAdapter;
import com.sintef_energy.ubisolar.adapter.SimilarAdapter;
import com.sintef_energy.ubisolar.adapter.YourAdapter;
import com.sintef_energy.ubisolar.fragments.DefaultTabFragment;
import com.sintef_energy.ubisolar.fragments.social.SocialCompareFragment;
import com.sintef_energy.ubisolar.fragments.social.SocialFriendListFragment;
import com.sintef_energy.ubisolar.model.Residence;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.model.User;

import java.util.ArrayList;

/**
 * Created by perok on 21.03.14.
 */
public class SocialFragment extends DefaultTabFragment {

    private static final String TAG = SocialFragment.class.getName();
    public static final String TAB_WORDS = "tips";
    public static final String TAB_NUMBERS = "your";

    private View mRoot;
    private TabHost mTabHost;
    private int mCurrentTab;
    private FriendAdapter friendAdapter;
    private SimilarAdapter similarAdapter;

    public static SocialFragment newInstance(int sectionNumber) {
        SocialFragment fragment = new SocialFragment();
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
        similarAdapter = new SimilarAdapter(getActivity(), R.layout.fragment_social_row, new ArrayList<Residence>());
        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) mRoot.findViewById(R.id.fragment_social_pager);
        pager.setAdapter(new MyPagerAdapter(getFragmentManager(), friendAdapter, similarAdapter));
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

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = { "Friends", "Similar"};
        private FriendAdapter friendAdapter;
        private SimilarAdapter similarAdapter;
        public MyPagerAdapter(FragmentManager fm, FriendAdapter friendAdapter, SimilarAdapter similarAdapter) {
            super(fm);
            this.friendAdapter = friendAdapter;
            this.similarAdapter = similarAdapter;
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
                    return SocialFriendListFragment.newInstance(0, friendAdapter);
                case 1:
                    return SocialCompareFragment.newInstance(1, similarAdapter);
                default:
                    return null;
            }
        }

    }
}
