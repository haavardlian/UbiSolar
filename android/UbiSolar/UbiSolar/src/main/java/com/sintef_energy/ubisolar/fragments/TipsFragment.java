package com.sintef_energy.ubisolar.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.TipAdapter;
import com.sintef_energy.ubisolar.dialogs.TipDialog;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.presenter.RequestManager;

import java.util.ArrayList;

/**
 * Created by Håvard on 22.03.2014.
 */
public class TipsFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private ListView tipsView;
    private TipAdapter tipAdapter;

    public static TipsFragment newInstance(int position) {
        TipsFragment fragment = new TipsFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tip_list, container, false);
        tipAdapter = new TipAdapter(getActivity(), R.layout.fragment_tip_row, new ArrayList<Tip>());

        tipsView = (ListView) rootView.findViewById(R.id.tipList);
        tipsView.setAdapter(tipAdapter);
        tipsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TipDialog dialog = new TipDialog(tipAdapter.getItem(i));
                dialog.setTargetFragment(TipsFragment.this, 0);
                dialog.show(getFragmentManager(), "tipDialog");
            }
        });

        //Get all tips from server asynchronously
        getActivity().setProgressBarIndeterminateVisibility(true);
        RequestManager.getInstance().doTipRequest().getAllTips(tipAdapter, this);
        return rootView;
    }

    public TipAdapter getAdapter() { return this.tipAdapter; }
}
