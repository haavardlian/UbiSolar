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
import com.sintef_energy.ubisolar.fragments.DefaultTabFragment;
import com.sintef_energy.ubisolar.model.User;

import java.util.ArrayList;

/**
 * Created by baier on 3/21/14.
 */
public class SocialFriendListFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String TAG = SocialFragment.class.getName();

    private ArrayList<User> friends;
    private static final String ARG_POSITION = "position";
    private View view;
    private FriendAdapter friendAdapter;



    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SocialFriendListFragment newInstance(int position, FriendAdapter friendAdapter) {
        SocialFriendListFragment fragment = new SocialFriendListFragment(friendAdapter);
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    public SocialFriendListFragment(FriendAdapter friendAdapter) {
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
        friends = new ArrayList<User>();
        FriendAdapter friendAdapter = new FriendAdapter(getActivity(),R.layout.fragment_social_friends_row, friends);
        ListView friendsList = (ListView) view.findViewById(R.id.social_list);
        friendsList.setAdapter(friendAdapter);

        friends.add(new User("Beate", getActivity().getResources().getDrawable(R.drawable.profile)));
        friends.add(new User("Håvi", getActivity().getResources().getDrawable(R.drawable.heat)));
        friends.add(new User("Piai", getActivity().getResources().getDrawable(R.drawable.profile)));
        friends.add(new User("Peri", getActivity().getResources().getDrawable(R.drawable.profile)));

        friendAdapter.notifyDataSetChanged();

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