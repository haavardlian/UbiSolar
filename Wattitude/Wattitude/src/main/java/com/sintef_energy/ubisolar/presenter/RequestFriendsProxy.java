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

package com.sintef_energy.ubisolar.presenter;

import android.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sintef_energy.ubisolar.R;

import com.sintef_energy.ubisolar.adapter.NewsFeedAdapter;
import com.sintef_energy.ubisolar.model.NewsFeedPost;
import com.sintef_energy.ubisolar.utils.Global;
import com.sintef_energy.ubisolar.utils.JsonObjectRequestTweaked;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Lars Erik on 01.05.2014.
 */
public class RequestFriendsProxy {

    private RequestQueue requestQueue;
    private ObjectMapper mapper;

    // package access constructor
    RequestFriendsProxy(RequestQueue requestQueue) {
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        this.requestQueue = requestQueue;
    }

    /**
     * Get wall updates
     * @param adapter Adapter to hold data
     * @param userId User id for user
     * @param fragment Fragment to run result on UI thread
     * @param friendIds List of friend ids to get updates for
     */
    public void getWallUpdates(final NewsFeedAdapter adapter, long userId, final Fragment fragment,
                               final String friendIds) {
        String url = Global.BASE_URL + "/user/" + userId + "/friends/wall?friends=" + friendIds;
        JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray jsonArray) {
                adapter.clear();
                for(int i = 0; i < jsonArray.length(); i++) {
                    try {
                        adapter.add(mapper.readValue(jsonArray.get(i).toString(), NewsFeedPost.class));
                    } catch (IOException | JSONException e) {
                        Log.e("REQUEST", "Error in JSON Mapping:");
                        Log.e("REQUEST", e.toString());
                    }
                }

                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(fragment.getActivity().getApplicationContext(),
                                fragment.getString(R.string.energy_saving_server_error),
                                Toast.LENGTH_LONG).show();
                    }
                });

                Log.e("REQUEST", "Error from server!!");
            }
        });

        requestQueue.add(jsonRequest);

        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Create a wall post
     * @param post The post to create
     * @param fragment Fragment to run result on UI thread
     */
    public void createWallPost(NewsFeedPost post, final Fragment fragment) {
        String url = Global.BASE_URL + "/user/" + post.getUserId() + "/friends/wall";
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(mapper.writeValueAsString(post));
        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequestTweaked jsonRequest = new JsonObjectRequestTweaked(Request.Method.PUT, url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();


                    }
                });

        requestQueue.add(jsonRequest);
    }

}
