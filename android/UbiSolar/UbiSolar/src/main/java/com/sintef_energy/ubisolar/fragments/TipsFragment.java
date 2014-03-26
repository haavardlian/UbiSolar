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
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.adapter.TipAdapter;
import com.sintef_energy.ubisolar.adapter.YourAdapter;
import com.sintef_energy.ubisolar.dialogs.TipDialog;
import com.sintef_energy.ubisolar.model.Tip;

import java.util.ArrayList;

/**
 * Created by Håvard on 22.03.2014.
 */
public class TipsFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private ListView tipsView;
    private YourAdapter yourAdapter;

    public static TipsFragment newInstance(int position, YourAdapter yourAdapter) {
        TipsFragment fragment = new TipsFragment(yourAdapter);
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    public TipsFragment(YourAdapter yourAdapter) {
        this.yourAdapter = yourAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tip_list, container, false);
        final TipAdapter tipAdapter = new TipAdapter(getActivity(), R.layout.fragment_tip_row, new ArrayList<Tip>());

        tipsView = (ListView) rootView.findViewById(R.id.tipList);
        tipsView.setAdapter(tipAdapter);
        tipsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TipDialog dialog = new TipDialog(tipAdapter.getItem(i), yourAdapter);
                dialog.show(getFragmentManager(), "tipDialog");
            }
        });

        //Get all tips from server asynchronously
        getActivity().setProgressBarIndeterminateVisibility(true);
        ((DrawerActivity) getActivity()).getTipPresenter().getAllTips(tipAdapter);
        return rootView;
    }
}
