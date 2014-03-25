package com.sintef_energy.ubisolar.fragments.social;

        import android.app.Activity;
        import android.app.Fragment;
        import android.app.LoaderManager;
        import android.content.CursorLoader;
        import android.content.Loader;
        import android.database.Cursor;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ListView;
        import android.widget.SimpleCursorAdapter;

        import com.sintef_energy.ubisolar.R;
        import com.sintef_energy.ubisolar.activities.DrawerActivity;
        import com.sintef_energy.ubisolar.adapter.FriendAdapter;
        import com.sintef_energy.ubisolar.adapter.SimilarAdapter;
        import com.sintef_energy.ubisolar.fragments.DefaultTabFragment;
        import com.sintef_energy.ubisolar.model.Residence;
        import com.sintef_energy.ubisolar.model.User;

        import java.util.ArrayList;

/**
 * Created by baier on 3/21/14.
 */
public class SocialCompareFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String TAG = SocialCompareFragment.class.getName();

    private SimpleCursorAdapter adapter;
    private View view;
    //private FriendAdapter friendAdapter;
    private SimilarAdapter similarAdapter;
    private ArrayList<Residence> houseDescription;
    private static final String ARG_POSITION = "position";


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SocialCompareFragment newInstance(int position, SimilarAdapter similarAdapter) {
            SocialCompareFragment fragment = new SocialCompareFragment(similarAdapter);
            Bundle b = new Bundle();
            b.putInt(ARG_POSITION, position);
            fragment.setArguments(b);
            return fragment;
        }

        public SocialCompareFragment(SimilarAdapter similarAdapter) {
            this.similarAdapter = similarAdapter;
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
            view = inflater.inflate(R.layout.fragment_social_friends, container, false);
            houseDescription = new ArrayList<Residence>();
            SimilarAdapter similarAdapter = new SimilarAdapter(getActivity(),R.layout.fragment_social_friends_row, houseDescription);
            ListView houseDescrList = (ListView) view.findViewById(R.id.social_list);
            houseDescrList.setAdapter(similarAdapter);

            houseDescription.add(new Residence("Beate", 1, 40, 7040, 'A'));


            similarAdapter.notifyDataSetChanged();

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

