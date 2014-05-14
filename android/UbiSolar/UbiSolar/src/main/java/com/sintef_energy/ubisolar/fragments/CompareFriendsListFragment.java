package com.sintef_energy.ubisolar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.Request;
import com.facebook.Response;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.FriendAdapter;
import com.sintef_energy.ubisolar.adapter.SimilarAdapter;
import com.sintef_energy.ubisolar.model.User;
import com.sintef_energy.ubisolar.presenter.RequestManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by baier on 3/21/14.
 */
public class CompareFriendsListFragment extends Fragment/* implements LoaderManager.LoaderCallbacks<Cursor>*/{
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
    public static CompareFriendsListFragment newInstance(int position) {
        CompareFriendsListFragment fragment = new CompareFriendsListFragment();
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
        view = inflater.inflate(R.layout.fragment_social_friends, container, false);
        friends = new ArrayList<>();
        friendAdapter = new FriendAdapter(getActivity(),R.layout.fragment_social_friends_row, friends);
        final ListView friendsList = (ListView) view.findViewById(R.id.social_list);
        friendsList.setAdapter(friendAdapter);
        //RequestManager.getInstance().doFriendRequest().getAllUsers(friendAdapter, this);

        friendsList.setClickable(true);
        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Fragment fragment = CompareFriendsFragment.newInstance(position, friendAdapter.getItem(position));
                addFragment(fragment, true, friends.get(position));

            }
        });

        populateFriendList(friendAdapter);
        return view;
    }

    public void populateFriendList(final FriendAdapter friendAdapter) {
        Request.Callback callback = new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                if(response.getError() != null)
                    return;
                try {
                    JSONArray friends = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
                    friendAdapter.clear();
                    for(int i = 0; i < friends.length(); i++) {
                        JSONObject friend = friends.getJSONObject(i);
                        if(friend.has("installed") && friend.getBoolean("installed"))
                            friendAdapter.add(new User(friend.getLong("id"), friend.getString("name")));
                    }

                    friendAdapter.notifyDataSetChanged();
                } catch(Exception e) {

                }
            }
        };

        RequestManager.getInstance().doFacebookRequest().getFriends(callback);
    }

    public FriendAdapter getAdapter() {
        return friendAdapter;
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, User user) {
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
               .replace(R.id.container, fragment, "Compare")
               .addToBackStack(null)
               .commit();
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
/*
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                EnergyContract.Users.CONTENT_URI,
                EnergyContract.Users.PROJECTION_ALL,
                null,
                null,
                UserModel.UserEntry._ID + " ASC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        friends.clear();

        cursor.moveToFirst();
        if (cursor.getCount() != 0)
            do {
                UserModel model = new UserModel(cursor);
                friends.add(model);
            } while (cursor.moveToNext());

        friendAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        friends.clear();
    }
*/


}