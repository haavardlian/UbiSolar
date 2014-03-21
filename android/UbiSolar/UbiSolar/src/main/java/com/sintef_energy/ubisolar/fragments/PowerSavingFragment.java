package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.TipAdapter;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.dialogs.AddTipDialog;
import com.sintef_energy.ubisolar.model.Tip;

import java.util.ArrayList;

/**
 * Created by perok on 2/11/14.
 */
public class PowerSavingFragment extends DefaultTabFragment {
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    ListView tipsView;
    public static PowerSavingFragment newInstance(int sectionNumber) {
        PowerSavingFragment fragment = new PowerSavingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PowerSavingFragment() {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_add_tip:
                displayAddTipDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_tip, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_power_saving, container, false);
        TipAdapter tipAdapter = new TipAdapter(getActivity(), R.layout.fragment_tip_row, new ArrayList<Tip>());

        tipsView = (ListView) rootView.findViewById(R.id.tipList);
        tipsView.setAdapter(tipAdapter);

        //Get all tips from server asynchronously
        getActivity().setProgressBarIndeterminateVisibility(true);
        ((DrawerActivity) getActivity()).getTipPresenter().getAllTips(tipAdapter);
        return rootView;
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

    public void displayAddTipDialog(){
        AddTipDialog dialog = new AddTipDialog();
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "addTipDialog");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
