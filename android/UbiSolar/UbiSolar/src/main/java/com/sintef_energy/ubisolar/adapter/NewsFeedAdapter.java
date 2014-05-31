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
import com.sintef_energy.ubisolar.model.NewsFeedPost;
import com.sintef_energy.ubisolar.presenter.RequestManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Adapter for managing and populating the homepage news feed
 */
public class NewsFeedAdapter extends ArrayAdapter<NewsFeedPost> {
    private Context mContext;
    private int mLayoutResourceId;
    private List<NewsFeedPost> mNewsFeed;

    public NewsFeedAdapter(Context context, int layoutResourceId, ArrayList<NewsFeedPost> newsFeed) {
        super(context, layoutResourceId);
        this.mContext = context;
        this.mLayoutResourceId = layoutResourceId;
        this.mNewsFeed = newsFeed;
    }

    public Activity getActivity() {
        return (Activity) mContext;
    }

    @Override
    public void add(NewsFeedPost object) {
        mNewsFeed.add(object);
    }

    @Override
    public void clear() {
        mNewsFeed.clear();
    }

    @Override
    public NewsFeedPost getItem(int position) {
        return mNewsFeed.get(position);
    }

    @Override
    public int getCount() {
        return mNewsFeed.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsFeedPostHolder holder;

        String[] messages =  mContext.getResources().getStringArray(R.array.wall_post_messages);

        //Load the views
        if(convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new NewsFeedPostHolder();
            holder.name = (TextView)convertView.findViewById(R.id.wall_item_name);
            holder.message = (TextView)convertView.findViewById(R.id.wall_item_text);
            holder.date = (TextView) convertView.findViewById(R.id.wall_item_date);
            holder.time = (TextView) convertView.findViewById(R.id.wall_item_time);
            holder.profilePic = (ProfilePictureView) convertView.findViewById(R.id.wall_item_pic);

            convertView.setTag(holder);
        } else {
            holder = (NewsFeedPostHolder)convertView.getTag();
        }

        //Populate the views
        if(!mNewsFeed.isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");

            NewsFeedPost post = mNewsFeed.get(position);
            Date date = new Date(post.getTimestamp() * 1000);
            holder.message.setText(messages[post.getMessage()-1]);
            holder.date.setText(dateFormat.format(date));
            holder.time.setText(timeFormat.format(date));
            if(!holder.profilePicLoaded) {
                holder.profilePic.setProfileId(String.valueOf(post.getUserId()));
                holder.profilePicLoaded = true;
            }
            if(!holder.nameLoaded) {
                RequestManager.getInstance().doFacebookRequest().getFacebookName(post.getUserId(),
                        holder.name);
                holder.nameLoaded = true;
            }
        }
        return convertView;
    }

    /**
     * Holder for holding the news feed views
     */
    static class NewsFeedPostHolder {
        boolean profilePicLoaded = false;
        ProfilePictureView profilePic;
        TextView name;
        boolean nameLoaded = false;
        TextView message;
        TextView date;
        TextView time;
    }
}
