package com.sintef_energy.ubisolar.presenter;

import android.app.Activity;
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
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.adapter.TipAdapter;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.model.TipRating;
import com.sintef_energy.ubisolar.utils.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Håvard on 19.03.14.
 */
public class TipPresenter {
    String tag = "SERVER";
    RequestQueue requestQueue;
    ObjectMapper mapper;
    public TipPresenter(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    }

    public void getAllTips(final TipAdapter adapter)
    {
        String url = Global.BASE_URL + "/tips";
        JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                adapter.clear();
                for(int i = 0; i < jsonArray.length(); i++) {
                    try {
                        adapter.add((Tip)mapper.readValue(jsonArray.get(i).toString(), Tip.class));
                        //Log.d(tag, adapter.getItem(i).toString());
                    } catch (IOException | JSONException e) {
                        Log.e(tag, "Error in JSON Mapping:");
                        Log.e(tag, e.toString());
                    }
                }
                adapter.notifyDataSetChanged();
                adapter.getActivity().setProgressBarIndeterminateVisibility(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                adapter.getActivity().setProgressBarIndeterminateVisibility(false);
                Toast.makeText(adapter.getActivity().getApplicationContext(), "Could not get data from server",
                        Toast.LENGTH_LONG).show();
                Log.e(tag, "Error from server!!");
            }
        });

        requestQueue.add(jsonRequest);
    }

    public void createTip(final Activity activity, Tip tip) {
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
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(activity.getApplicationContext(), response.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity.getApplicationContext(), "An error occured",
                                Toast.LENGTH_LONG).show();
                    }
                });

        ((DrawerActivity) activity).getRequestQueue().add(jsonRequest);
    }

    public void createRating(final Activity activity, TipRating rating) {
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
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(activity.getApplicationContext(), response.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity.getApplicationContext(), "An error occured",
                                Toast.LENGTH_LONG).show();
                    }
                });

        ((DrawerActivity) activity).getRequestQueue().add(jsonRequest);
    }

}
