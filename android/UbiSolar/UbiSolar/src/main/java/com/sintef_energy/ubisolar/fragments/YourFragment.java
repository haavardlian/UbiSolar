package com.sintef_energy.ubisolar.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.TipAdapter;
import com.sintef_energy.ubisolar.adapter.YourAdapter;
import com.sintef_energy.ubisolar.dialogs.TipDialog;
import com.sintef_energy.ubisolar.dialogs.YourDialog;
import com.sintef_energy.ubisolar.model.Tip;

import java.util.ArrayList;

/**
 * Created by Håvard on 24.03.2014.
 */
public class YourFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private int position;
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

        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_your_list, container, false);
        ListView yourList = (ListView) rootView.findViewById(R.id.yourList);
        yourAdapter = new YourAdapter(getActivity(), R.layout.fragment_your_row, new ArrayList<Tip>(), getFragmentManager());
        yourList.setAdapter(yourAdapter);

        return rootView;
    }

    public YourAdapter getAdapter() { return yourAdapter; }
}
