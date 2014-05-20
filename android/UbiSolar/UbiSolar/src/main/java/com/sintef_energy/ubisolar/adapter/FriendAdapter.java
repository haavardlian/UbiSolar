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
 * Created by baier on 3/21/14.
 */
public class FriendAdapter extends ArrayAdapter<User> {

    private Context context;
    private int resource;
    private List<User> users = null;

    public FriendAdapter(Context context, int resource, List<User> users) {
        super(context, resource);

        this.context = context;
        this.resource = resource;
        this.users = users;
    }

    @Override
    public void add(User object) {
        users.add(object);
    }

    @Override
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FriendHolder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new FriendHolder();
            holder.name = (TextView)row.findViewById(R.id.social_user_name);
            holder.profilePic = (ProfilePictureView)row.findViewById(R.id.social_profile_pic);

            row.setTag(holder);
        } else {
            holder = (FriendHolder)row.getTag();
        }

        if(!users.isEmpty()) {
            User user = users.get(position);
            holder.name.setText(user.getName());
            holder.profilePic.setProfileId(String.valueOf(user.getUserId()));
        }
        return row;
    }

    static class FriendHolder {
        TextView name;
        ProfilePictureView profilePic;
    }

}
