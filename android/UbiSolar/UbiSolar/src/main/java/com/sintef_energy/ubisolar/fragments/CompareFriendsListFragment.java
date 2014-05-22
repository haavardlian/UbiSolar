/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.sintef_energy.ubisolar.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.Request;
import com.facebook.Response;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.FriendAdapter;
import com.sintef_energy.ubisolar.model.User;
import com.sintef_energy.ubisolar.presenter.RequestManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Fragment with a list of friends
 */
public class CompareFriendsListFragment extends Fragment{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String TAG = CompareFriendsListFragment.class.getName();
    private static final String ARG_POSITION = "position";

    private ArrayList<User> mFriends;
    private View mView;
    private FriendAdapter mFriendAdapter;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mView == null)
            mView = inflater.inflate(R.layout.fragment_social_friends, container, false);

        if(mFriends == null)
            mFriends = new ArrayList<>();

        if(mFriendAdapter == null) {
            mFriendAdapter = new FriendAdapter(getActivity(), R.layout.fragment_social_friends_row, mFriends);
            final ListView friendsList = (ListView) mView.findViewById(R.id.social_list);
            friendsList.setAdapter(mFriendAdapter);

            friendsList.setClickable(true);
            friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Fragment fragment = CompareFriendsFragment.newInstance(position, mFriendAdapter.getItem(position));
                    addFragment(fragment, true, mFriends.get(position).getName());

                }
            });
        }

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        populateFriendList(mFriendAdapter);
    }

    /**
     * Sends a query to facebook and gets the users friends
     * Loops through the list of friends and populates the list with friends
     * that has the application installed
     * @param friendAdapter The adapter to be filled
     */
    public void populateFriendList(final FriendAdapter friendAdapter) {
        friendAdapter.clear();

        //Callback to get friends from facebook
        Request.Callback callback = new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                if(response.getError() != null)
                    return;
                try {
                    JSONArray friends = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
                    friendAdapter.clear();

                    //Add friends that has the application installed
                    for(int i = 0; i < friends.length(); i++) {
                        JSONObject friend = friends.getJSONObject(i);
                        if(friend.has("installed") && friend.getBoolean("installed"))
                            friendAdapter.add(new User(friend.getLong("id"), friend.getString("name")));
                    }

                    friendAdapter.notifyDataSetChanged();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };

        RequestManager.getInstance().doFacebookRequest().getFriends(callback);
    }

    /**
     * Returns the friend adapter
     * @return FriendAdapter
     */
    public FriendAdapter getAdapter() {
        return mFriendAdapter;
    }

    /**
     * Swaps the active fragment
     * @param fragment The new fragment
     * @param addToBackStack Whether or not the fragment should be added to the back stack
     * @param tag The fragments tag.
     */
    public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        ft.replace(R.id.container, fragment, "Compare");

        if(addToBackStack)
               ft.addToBackStack(tag);

        ft.commit();
    }
}