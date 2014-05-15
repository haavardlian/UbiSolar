package com.sintef_energy.ubisolar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.devspark.progressfragment.ProgressFragment;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.YourAdapter;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.presenter.RequestManager;

import java.util.ArrayList;

/**
 * Created by Håvard on 24.03.2014.
 */
public class YourFragment extends ProgressFragment {
    private static final String ARG_POSITION = "position";
    private YourAdapter yourAdapter;

    public static YourFragment newInstance(int position) {
        YourFragment fragment = new YourFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_your_list, container, false);
        ListView yourList = (ListView) rootView.findViewById(R.id.yourList);
        yourAdapter = new YourAdapter(getActivity(), R.layout.fragment_your_row, new ArrayList<Tip>(), getFragmentManager());
        yourList.setAdapter(yourAdapter);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RequestManager.getInstance().doTipRequest().getSavedTips(yourAdapter, this);
        setEmptyText("You have no saved tips"); //TODO insert in xml/strings
        setContentEmpty(true);
        setContentShown(false);
    }

    public YourAdapter getAdapter() { return yourAdapter; }
}
