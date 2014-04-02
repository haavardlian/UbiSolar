package com.sintef_energy.ubisolar.fragments.social;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.SimilarAdapter;

/**
 * Created by baier on 4/1/14.
 */
public class CompareSimilarFragment extends Fragment{

    /* The fragment argument representing the section number for this
     * fragment.
     */
    public static final String TAG = CompareSimilarFragment.class.getName();

    private static final String ARG_POSITION = "position";
    private View view;


    private SimilarAdapter similarAdapter;

    public CompareSimilarFragment(SimilarAdapter similarAdapter) {
        this.similarAdapter = similarAdapter;
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CompareSimilarFragment newInstance(int position, SimilarAdapter similarAdapter) {
        CompareSimilarFragment fragment = new CompareSimilarFragment(similarAdapter);
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }


    /**
     * The first call to a created fragment
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_social_compare, container, false);



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
    public void onDestroy(){
        super.onDestroy();
    }

}
