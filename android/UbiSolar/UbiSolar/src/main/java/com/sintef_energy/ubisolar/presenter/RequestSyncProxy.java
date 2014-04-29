package com.sintef_energy.ubisolar.presenter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.model.Device;
import com.sintef_energy.ubisolar.utils.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Per Øyvind Kanestrøm on 29.04.14.
 *
 * Sync to and from server.
 *
 * Example call: http://188.226.188.11:1337/user/{user}/sync/device/frontend/{timestamp}
 *
 */
public class RequestSyncProxy {

    private static final String TAG = RequestSyncProxy.class.getName();

    private RequestQueue requestQueue;
    private ObjectMapper mapper;

    private Context context;

    // package access constructor
    RequestSyncProxy(Context context, RequestQueue requestQueue) {
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        this.context = context;
        this.requestQueue = requestQueue;
    }

    public ArrayList<DeviceModel> getBackendSync(long userId, long timestamp) {

        RequestFuture<JSONArray> future = RequestFuture.newFuture();

        ArrayList<DeviceModel> dModels = new ArrayList<>();

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("http").encodedAuthority(Global.URL)
                .appendPath("user").appendPath(""+userId)
                .appendPath("sync").appendPath("device")
                .appendPath("" + timestamp);

        String url = builder.build().toString();

        Log.v(TAG, "Request to URL: " + url);
        // Perform request
        JsonArrayRequest jsonRequest = new JsonArrayRequest(url, future, future);

        requestQueue.add(jsonRequest);

        // Handle response synchronous.
        try {
            JSONArray response = future.get(); // this will block

            Log.v(TAG, "Response: " + response);

            for (int i = 0; i < response.length(); i++) {
                    dModels.add((DeviceModel) mapper.readValue(response.get(i).toString(), DeviceModel.class));
                    //Log.d(tag, adapter.getItem(i).toString());
            }

            return dModels;
        }catch (IOException | JSONException e) {
            Log.e(TAG, "Error in JSON Mapping:");
            Log.e(TAG, e.toString());
        }
        catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException:\n" + e.getMessage());
            e.printStackTrace();
        }
        catch (ExecutionException e) {

            Log.e(TAG, "ExecutionException:\n" + e.getMessage());

            //TODO: Handle no data error correctly..
            e.printStackTrace();
            return dModels;
        }

        //Toast.makeText(context, "Could not get data from server",
        //Toast.LENGTH_LONG).show();
        Log.e(TAG, "Error from server!!");

        return null;
    }

    /*
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
                        try {
                            Toast.makeText(context, response.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
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
                        try {
                            Toast.makeText(context, response.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "An error occurred",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonRequest);
    }*/
}
