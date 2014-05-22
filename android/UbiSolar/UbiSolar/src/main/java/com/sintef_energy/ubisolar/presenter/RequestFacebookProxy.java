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

package com.sintef_energy.ubisolar.presenter;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.sintef_energy.ubisolar.adapter.NewsFeedAdapter;
import com.sintef_energy.ubisolar.model.NewsFeedPost;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

/**
 * Created by Håvard on 07.05.14.
 */
public class RequestFacebookProxy {

    public void postPicture(final Fragment fragment, String caption, byte[] image) {
        Bundle param = new Bundle();
        param.putString("message", caption);
        param.putByteArray("picture", image);
        RequestAsyncTask requestTask = new Request(Session.getActiveSession(), "me/photos",
                param, HttpMethod.POST, new Request.Callback() {
            @Override
            public void onCompleted(final Response response) {

                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (response.getError() == null) {
                            Toast.makeText(fragment.getActivity(),
                                    "Posted to Facebook", Toast.LENGTH_SHORT).show();

                            RequestManager.getInstance().doFriendRequest().createWallPost(
                                    new NewsFeedPost(0,
                                            Long.valueOf(PreferencesManager
                                                    .getInstance().getKeyFacebookUid()), 2,
                                            System.currentTimeMillis()/1000), fragment);
                        } else
                            Toast.makeText(fragment.getActivity(),
                                    "Error posting to Facebook", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).executeAsync();
    }


    public void postMessage(final Fragment fragment, String message, String name) {
        Bundle param = new Bundle();
        param.putString("message", name + " - " + message);
        RequestAsyncTask requestTask = new Request(Session.getActiveSession(), "me/feed", param,
                HttpMethod.POST, new Request.Callback() {
            @Override
            public void onCompleted(final Response response) {
                if(response.getError() == null) {
                    Log.d("FACEBOOK", "Message posted");
                    RequestManager.getInstance().doFriendRequest().createWallPost(
                            new NewsFeedPost(0,
                                    Long.valueOf(PreferencesManager
                                            .getInstance().getKeyFacebookUid()), 3,
                                    System.currentTimeMillis()/1000), fragment);
                } else
                    Log.d("FACEBOOK", "Error");

            }
        }).executeAsync();
    }

    public void getFacebookName(long userId, final TextView textView) {
        new Request(Session.getActiveSession(), "/" + userId, null, HttpMethod.GET,
                new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        if(response.getError() == null) {
                            try {
                                String name = response.getGraphObject().getInnerJSONObject().getString("name");
                                textView.setText(name);
                            } catch (JSONException e) {
                                textView.setText("Error");
                                e.printStackTrace();
                            }
                        }
                    }
        }).executeAsync();
    }

    public void getFriends(Request.Callback callback) {
        Bundle param = new Bundle();
        param.putString("fields", "name, installed");
        new Request(Session.getActiveSession(), "me/friends", param, HttpMethod.GET, callback)
                .executeAsync();
    }

    public void populateWall(final NewsFeedAdapter adapter, final Fragment fragment) {
        Request.Callback callback = new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                if(response.getError() != null)
                    return;
                try {
                    JSONArray friends = response.getGraphObject().getInnerJSONObject()
                            .getJSONArray("data");

                    ArrayList<String> friendIds = new ArrayList<>();

                    for(int i = 0; i < friends.length(); i++) {
                        JSONObject friend = friends.getJSONObject(i);
                        if(friend.has("installed") && friend.getBoolean("installed"))
                            friendIds.add(friend.getString("id"));
                    }
                    RequestManager.getInstance().doFriendRequest().getWallUpdates(adapter,
                            Long.valueOf(PreferencesManager.getInstance().getKeyFacebookUid()),
                            fragment, TextUtils.join(",", friendIds));
                } catch(Exception e) {

                }
            }
        };

        getFriends(callback);
    }

}
