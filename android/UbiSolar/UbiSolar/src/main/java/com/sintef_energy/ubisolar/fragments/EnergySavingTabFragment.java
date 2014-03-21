package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;


/**
 * Created by perok on 21.03.14.
 */
public class EnergySavingTabFragment extends DefaultTabFragment implements TabHost.OnTabChangeListener {

    private static final String TAG = EnergySavingTabFragment.class.getName();
    public static final String TAB_WORDS = "tips";
    public static final String TAB_NUMBERS = "your";

    private View mRoot;
    private TabHost mTabHost;
    private int mCurrentTab;

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
        mRoot = inflater.inflate(R.layout.fragment_energy_saving_tab, null);
        mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
        setupTabs();
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        mTabHost.setOnTabChangedListener(this);
        mTabHost.setCurrentTab(mCurrentTab);
        // manually start loading stuff in the first tab
        updateTab(TAB_WORDS, R.id.fragment_energy_saving_tab_tips);
    }

    private void setupTabs() {
        mTabHost.setup(); // you must call this before adding your tabs!
        mTabHost.addTab(newTab(TAB_WORDS, R.string.fragment_energy_saving_tab_tips, R.id.fragment_energy_saving_tab_tips));
        mTabHost.addTab(newTab(TAB_NUMBERS, R.string.fragment_energy_saving_tab_your, R.id.fragment_energy_saving_tab_your));
    }

    private TabHost.TabSpec newTab(String tag, int labelId, int tabContentId) {
        Log.d(TAG, "buildTab(): tag=" + tag);

        View indicator = LayoutInflater.from(getActivity()).inflate(
                R.layout.tab,
                (ViewGroup) mRoot.findViewById(android.R.id.tabs), false);

        ((TextView) indicator.findViewById(R.id.text)).setText(labelId);

        TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tag);
        tabSpec.setIndicator(indicator);
        tabSpec.setContent(tabContentId);

        return tabSpec;
    }

    @Override
    public void onTabChanged(String tabId) {
        Log.d(TAG, "onTabChanged(): tabId=" + tabId);
        if (TAB_WORDS.equals(tabId)) {
            updateTab(tabId, R.id.fragment_energy_saving_tab_tips);
            mCurrentTab = 0;
            return;
        }
        if (TAB_NUMBERS.equals(tabId)) {
            updateTab(tabId, R.id.fragment_energy_saving_tab_your);
            mCurrentTab = 1;
            return;
        }
    }

    private void updateTab(String tabId, int placeholder) {
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag(tabId) == null) {
            fm.beginTransaction()
                    .replace(placeholder, new PowerSavingFragment(), tabId)
                    .commit();
        }
    }

}
