package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.FriendAdapter;
import com.sintef_energy.ubisolar.adapter.SimilarAdapter;
import com.sintef_energy.ubisolar.model.User;

import java.util.ArrayList;

/**
 * Created by baier on 3/21/14.
 */
public class CompareFriendsListFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String TAG = CompareFriendsListFragment.class.getName();

    private ArrayList<User> friends;
    private static final String ARG_POSITION = "position";
    private View view;
    private FriendAdapter friendAdapter;
    private SimilarAdapter simAdapter;



    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CompareFriendsListFragment newInstance(int position, FriendAdapter friendAdapter) {
        CompareFriendsListFragment fragment = new CompareFriendsListFragment(friendAdapter);
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    public CompareFriendsListFragment(FriendAdapter friendAdapter) {
        this.friendAdapter = friendAdapter;
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
        friends = new ArrayList<>();
        FriendAdapter friendAdapter = new FriendAdapter(getActivity(),R.layout.fragment_social_friends_row, friends);
        final ListView friendsList = (ListView) view.findViewById(R.id.social_list);
        friendsList.setAdapter(friendAdapter);

        friends.add(new User("Beate"));
        friends.add(new User("Håvi"));
        friends.add(new User("Piai"));
        friends.add(new User("Peri"));

        friendAdapter.notifyDataSetChanged();

        friendsList.setClickable(true);
        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Fragment fragment = CompareFriendsFragment.newInstance(position, simAdapter);
                addFragment(fragment, true, friends.get(position));
            }
        });

        return view;
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        private final String[] TITLES = { "Friends", "Similar profiles"};
        private FriendAdapter friendAdapter;
        private SimilarAdapter simAdapter;
        public MyPagerAdapter(FragmentManager fm, FriendAdapter friendAdapter, SimilarAdapter simAdapter) {
            super(fm);
            this.friendAdapter = friendAdapter;
            this.simAdapter = simAdapter;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return CompareFriendsListFragment.newInstance(0, friendAdapter);
                case 1:
                    return CompareSimilarFragment.newInstance(1, simAdapter);
                default:
                    return null;
            }
        }


    }

    public void addFragment(Fragment fragment, boolean addToBackStack, User user) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(user.getName());
        }

        ft.replace(R.id.container, fragment);
        ft.commit();
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