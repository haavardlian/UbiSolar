package com.sintef_energy.ubisolar.presenter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sintef_energy.ubisolar.adapter.FriendAdapter;
import com.sintef_energy.ubisolar.model.User;
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

    private Context context;
    private Activity activity;

    // package access constructor
    RequestFriendsProxy(Activity activity, RequestQueue requestQueue) {
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        this.activity = activity;
        this.requestQueue = requestQueue;
    }

    public void getAllUsers(final FriendAdapter adapter, final Fragment fragment) {
        String url = Global.BASE_URL + "/users";
        JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray jsonArray) {
                adapter.clear();

                for(int i = 0; i < jsonArray.length(); i++) {
                    try {
                        adapter.add(mapper.readValue(jsonArray.get(i).toString(), User.class));
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
                        fragment.getActivity().setProgressBarIndeterminateVisibility(false);
                    }
                });


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity.getApplicationContext(), "Could not get data from server", Toast.LENGTH_LONG).show();
                    }
                });

                Log.e("REQUEST", "Error from server!!");

                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragment.getActivity().setProgressBarIndeterminateVisibility(false);
                    }
                });
            }
        });

        requestQueue.add(jsonRequest);
    }


}
