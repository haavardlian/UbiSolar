package com.sintef_energy.ubisolar.fragments.social;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.ComparisonSettingsAdapter;
import com.sintef_energy.ubisolar.model.ResidenceAttributes;

import java.util.ArrayList;

/**
 * Created by baier on 4/1/14.
 */
public class CompareSimilarFragment extends Fragment {

    /* The fragment argument representing the section number for this
     * fragment.
     */
    public static final String TAG = CompareSimilarFragment.class.getName();

    private ComparisonSettingsAdapter compAdapter;

    private View view;
    private ArrayList<ResidenceAttributes> houseDescription;
    private static final String ARG_POSITION = "position";


    public CompareSimilarFragment(ComparisonSettingsAdapter compAdapter) {
        this.compAdapter = compAdapter;
    }

    public CompareSimilarFragment() {

    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CompareSimilarFragment newInstance(int position, ComparisonSettingsAdapter compAdapter) {
        CompareSimilarFragment fragment = new CompareSimilarFragment(compAdapter);
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

        houseDescription = new ArrayList<ResidenceAttributes>();

        houseDescription.add(new ResidenceAttributes("Area"));
        houseDescription.add(new ResidenceAttributes("Number of residents"));
        houseDescription.add(new ResidenceAttributes("Resident size"));
        houseDescription.add(new ResidenceAttributes("Energy class"));

        ComparisonSettingsAdapter compAdapter= new ComparisonSettingsAdapter(getActivity(),R.layout.fragment_similar_compare_row, houseDescription);
        ListView houseDescrList = (ListView) view.findViewById(R.id.comp_settings_list);
        houseDescrList.setAdapter(compAdapter);

        compAdapter.notifyDataSetChanged();

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