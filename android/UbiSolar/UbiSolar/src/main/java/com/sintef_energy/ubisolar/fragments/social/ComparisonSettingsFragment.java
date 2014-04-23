package com.sintef_energy.ubisolar.fragments.social;

        import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.sintef_energy.ubisolar.R;
        import com.sintef_energy.ubisolar.activities.DrawerActivity;
        import com.sintef_energy.ubisolar.adapter.ComparisonAdapter;
        import com.sintef_energy.ubisolar.fragments.DefaultTabFragment;
        import com.sintef_energy.ubisolar.model.ResidenceAttributes;

import java.util.ArrayList;

/**
 * Created by baier on 3/21/14.
 */
public class ComparisonSettingsFragment extends DefaultTabFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String TAG = ComparisonSettingsFragment.class.getName();

    private SimpleCursorAdapter adapter;
    private View view;
    private ComparisonAdapter compAdapter;
    private ArrayList<ResidenceAttributes> houseDescription;
    private static final String ARG_POSITION = "position";


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ComparisonSettingsFragment newInstance(int position) {
            ComparisonSettingsFragment fragment = new ComparisonSettingsFragment();
            Bundle b = new Bundle();
            b.putInt(ARG_SECTION_NUMBER, position);
            fragment.setArguments(b);
            return fragment;
        }

        public ComparisonSettingsFragment() {
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
            view = inflater.inflate(R.layout.fragment_settings_comparison, container, false);
            houseDescription = new ArrayList<ResidenceAttributes>();

            houseDescription.add(new ResidenceAttributes("Area"));
            houseDescription.add(new ResidenceAttributes("Number of residents"));
            houseDescription.add(new ResidenceAttributes("Resident size"));
            houseDescription.add(new ResidenceAttributes("Energy class"));


            ComparisonAdapter compAdapter= new ComparisonAdapter(getActivity(),R.layout.fragment_settings_comparison_row, houseDescription);
            ListView houseDescrList = (ListView) view.findViewById(R.id.social_similar_list);
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
        }

        @Override
        public void onDestroy(){
            super.onDestroy();
        }

    }

