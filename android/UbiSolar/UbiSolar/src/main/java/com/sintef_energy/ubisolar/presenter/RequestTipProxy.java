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
import com.sintef_energy.ubisolar.adapter.TipAdapter;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.model.TipRating;
import com.sintef_energy.ubisolar.utils.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Håvard on 25.03.2014.
 */
public class RequestTipProxy {

    private RequestQueue requestQueue;
    private ObjectMapper mapper;

    private Activity activity;

    // package access constructor
    RequestTipProxy(Activity activity, RequestQueue requestQueue) {
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        this.activity = activity;
        this.requestQueue = requestQueue;
    }

    public void getAllTips(final TipAdapter adapter, final Fragment fragment) {
        String url = Global.BASE_URL + "/tips";
        JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray jsonArray) {
                adapter.clear();

                for(int i = 0; i < jsonArray.length(); i++) {
                    try {
                        adapter.add((Tip)mapper.readValue(jsonArray.get(i).toString(), Tip.class));
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

    public void createTip(Tip tip) {
        String url = Global.BASE_URL + "/tips";
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(mapper.writeValueAsString(tip));
        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(activity.getApplicationContext(), response.getString("message"),
                                            Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity.getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

        requestQueue.add(jsonRequest);
    }

    public void createRating(TipRating rating) {
        String url = Global.BASE_URL + "/tips/" + rating.getTipId() + "/rating/";
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(mapper.writeValueAsString(rating));
        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {

                            activity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(activity.getApplicationContext(), response.getString("message"),
                                            Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity.getApplicationContext(), "An error occurred",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

        requestQueue.add(jsonRequest);
    }
}
