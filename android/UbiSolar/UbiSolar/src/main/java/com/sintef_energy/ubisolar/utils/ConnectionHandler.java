package com.sintef_energy.ubisolar.utils;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sintef_energy.ubisolar.model.Device;
import com.sintef_energy.ubisolar.model.DeviceUsage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by thb on 17.03.14.
 */
public class ConnectionHandler {
    String tag = "CONNECTION";
    private final static String BASE_URL = "http://78.91.2.176:8080/";


    public void addDevice(Activity activity, long userID, Device device)
    {
        RequestQueue rq = Volley.newRequestQueue(activity);
        String url = BASE_URL + "/" + userID + "/devices";

        String json = "";
        JSONObject jsonObject = null;

        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            json = ow.writeValueAsString(device);
            jsonObject = (JSONObject) new JSONTokener(json).nextValue();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT,url,jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO display success message
                        Log.v(tag, response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(tag, error.toString());
                        //TODO display error
                        error.printStackTrace();
                    }
                });

        rq.add(jsonRequest);
    }

    public void pullDevices(Activity activity, long userID)
    {
        String url = BASE_URL + "/" + userID + "/devices";
        RequestQueue rq = Volley.newRequestQueue(activity);

        JsonArrayRequest jsonRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v(tag, "Successfully pulled devices");
                        //TODO update local devices
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(tag, error.toString());
                //TODO display error
                error.printStackTrace();
            }
        });

        rq.add(jsonRequest);
    }

    public void addUsage(Activity activity, long userID, DeviceUsage usage)
    {
        RequestQueue rq = Volley.newRequestQueue(activity);
        //TODO fix url
        String url = BASE_URL + "/" + userID + "/usage/total";

        String json = "";
        JSONObject jsonObject = null;

        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            json = ow.writeValueAsString(usage);
            jsonObject = (JSONObject) new JSONTokener(json).nextValue();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT,url,jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //TODO display message
                            Log.v(tag, response.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO display error
                        error.printStackTrace();
                    }
                });

        rq.add(jsonRequest);
    }
}
