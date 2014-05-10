package com.sintef_energy.ubisolar.presenter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import com.sintef_energy.ubisolar.adapter.FriendAdapter;

import com.sintef_energy.ubisolar.adapter.WallAdapter;
import com.sintef_energy.ubisolar.model.User;
import com.sintef_energy.ubisolar.model.WallPost;
import com.sintef_energy.ubisolar.utils.Global;

import org.json.JSONArray;
import org.json.JSONException;

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
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        this.requestQueue = requestQueue;
    }

    public void getWallUpdates(final WallAdapter adapter, long userId, final Fragment fragment) {
        String url = Global.BASE_URL + "/user/" + userId + "/wall";
        JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray jsonArray) {
                adapter.clear();

                for(int i = 0; i < jsonArray.length(); i++) {
                    try {
                        adapter.add(mapper.readValue(jsonArray.get(i).toString(), WallPost.class));
                        //Log.d(tag, adapter.getItem(i).toString());
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
                                fragment.getString(R.string.energy_saving_server_error), Toast.LENGTH_LONG).show();
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


}
