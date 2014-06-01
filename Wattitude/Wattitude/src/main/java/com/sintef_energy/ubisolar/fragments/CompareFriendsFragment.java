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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.widget.ProfilePictureView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.User;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;

/**
 * Fragment for comparing your usage with a selected mFriend's usage
 */
public class CompareFriendsFragment extends Fragment {

    public static final String TAG = CompareFriendsFragment.class.getName();
    private static final String ARG_POSITION = "position";

    private User mFriend;

    private CompareFriendsFragment(User friend) {
        super();
        this.mFriend = friend;
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CompareFriendsFragment newInstance(int position, User user) {
        CompareFriendsFragment fragment = new CompareFriendsFragment(user);
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Load the users profile picture
        View view = inflater.inflate(R.layout.fragment_compare_friend, container, false);
        ProfilePictureView profilePicture = (ProfilePictureView) view.findViewById(R.id.userProfilePic);
        profilePicture.setProfileId(PreferencesManager.getInstance().getKeyFacebookUid());
        profilePicture.setPresetSize(ProfilePictureView.LARGE);

        //Load the friends profile picture
        ProfilePictureView friendPicture = (ProfilePictureView) view.findViewById(R.id.friendProfilePic);
        friendPicture.setProfileId(String.valueOf(this.mFriend.getUserId()));
        friendPicture.setPresetSize(ProfilePictureView.LARGE);

        return view;
    }
}