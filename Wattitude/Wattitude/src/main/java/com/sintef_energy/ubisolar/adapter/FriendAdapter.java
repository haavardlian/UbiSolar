/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.sintef_energy.ubisolar.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.User;

import java.util.List;

/**
 * Adapter managing the list of friends used for comparison
 */
public class FriendAdapter extends ArrayAdapter<User> {

    private Context mContext;
    private int mResource;
    private List<User> mUsers;

    /**
     * Constructor
     * @param context The parent activity
     * @param resource
     * @param users List of users to populate the view
     */
    public FriendAdapter(Context context, int resource, List<User> users) {
        super(context, resource);

        this.mContext = context;
        this.mResource = resource;
        this.mUsers = users;
    }

    @Override
    public void add(User object) {
        mUsers.add(object);
    }

    @Override
    public void clear() {
        mUsers.clear();
        notifyDataSetChanged();
    }

    @Override
    public User getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendHolder holder;

        //Get the views
        if(convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mResource, parent, false);

            holder = new FriendHolder();
            holder.name = (TextView)convertView.findViewById(R.id.social_user_name);
            holder.profilePic = (ProfilePictureView)convertView.findViewById(R.id.social_profile_pic);

            convertView.setTag(holder);
        } else {
            holder = (FriendHolder)convertView.getTag();
        }

        //Populate the views
        if(!mUsers.isEmpty()) {
            User user = mUsers.get(position);
            holder.name.setText(user.getName());
            holder.profilePic.setProfileId(String.valueOf(user.getUserId()));
        }
        return convertView;
    }

    /**
     * Static class to hold information
     */
    static class FriendHolder {
        TextView name;
        ProfilePictureView profilePic;
    }

}
