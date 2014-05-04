package com.sintef_energy.ubisolar.presenter;

import android.net.Uri;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyUsageModel;
import com.sintef_energy.ubisolar.utils.Global;
import com.sintef_energy.ubisolar.utils.JsonArrayRequestTweaked;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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

    // package access constructor
    RequestSyncProxy(RequestQueue requestQueue) {
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        this.requestQueue = requestQueue;
    }

    public ArrayList<DeviceModel> getBackendDeviceSync(long userId, long timestamp) {

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
        JsonArrayRequestTweaked jsonRequest = new JsonArrayRequestTweaked(JsonRequest.Method.GET, url, null, future, future);

        requestQueue.add(jsonRequest);

        // Handle response synchronous.
        try {
            JSONArray response = future.get(); // this will block

            for (int i = 0; i < response.length(); i++)
                    dModels.add(mapper.readValue(response.get(i).toString(), DeviceModel.class));

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

            e.printStackTrace();
        }

        //Toast.makeText(context, "Could not get data from server",
        //Toast.LENGTH_LONG).show();
        Log.e(TAG, "Error from server!!");

        return null;
    }


    /**
     *
     *
     * @param userId
     * @param deviceModels
     * @return ArrayList<DeviceModel> Success if size = 0
     */
    public ArrayList<DeviceModel> putFrontendDeviceSync(long userId, ArrayList<DeviceModel> deviceModels) {
        RequestFuture<JSONArray> future = RequestFuture.newFuture();

        ArrayList<DeviceModel> errorModels = new ArrayList<>();

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("http").encodedAuthority(Global.URL)
                .appendPath("user").appendPath("" + userId)
                .appendPath("sync").appendPath("device");

        String url = builder.build().toString();

        Log.v(TAG, "Request to URL: " + url);

        JSONArray data = new JSONArray();

        try {
            for(DeviceModel dModel : deviceModels)
                data.put(new JSONObject(mapper.writeValueAsString(dModel.getSerializeableDevice())));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequestTweaked request = new JsonArrayRequestTweaked(JsonRequest.Method.PUT, url, data.toString(), future, future);

        requestQueue.add(request);

        // Handle response synchronous.
        try {
            JSONArray response = future.get(); // this will block

            for (int i = 0; i < response.length(); i++) {
                errorModels.add(mapper.readValue(response.get(i).toString(), DeviceModel.class));
            }

            return errorModels;
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

            e.printStackTrace();
        }

        Log.e(TAG, "Error from server!!");

        return null;
    }



    public ArrayList<EnergyUsageModel> getBackendUsageSync(long userId, long timestamp) {

        RequestFuture<JSONArray> future = RequestFuture.newFuture();

        ArrayList<EnergyUsageModel> dModels = new ArrayList<>();

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("http").encodedAuthority(Global.URL)
                .appendPath("user").appendPath(""+userId)
                .appendPath("sync").appendPath("usage")
                .appendPath("" + timestamp);

        String url = builder.build().toString();

        Log.v(TAG, "Request to URL: " + url);
        // Perform request
        JsonArrayRequestTweaked jsonRequest = new JsonArrayRequestTweaked(JsonRequest.Method.GET, url, null, future, future);

        requestQueue.add(jsonRequest);

        // Handle response synchronous.
        try {
            JSONArray response = future.get(); // this will block

            for (int i = 0; i < response.length(); i++)
                    dModels.add(mapper.readValue(response.get(i).toString(), EnergyUsageModel.class));

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

            return dModels;
        }

        Log.e(TAG, "Error from server!!");

        return null;
    }


    /**
     *
     *
     * @param userId
     * @param deviceModels
     * @return ArrayList<DeviceModel> Success if size = 0
     */
    public ArrayList<EnergyUsageModel> putFrontendUsageSync(long userId, ArrayList<EnergyUsageModel> deviceModels) {
        RequestFuture<JSONArray> future = RequestFuture.newFuture();

        ArrayList<EnergyUsageModel> errorModels = new ArrayList<>();

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("http").encodedAuthority(Global.URL)
                .appendPath("user").appendPath("" + userId)
                .appendPath("sync").appendPath("usage");

        String url = builder.build().toString();

        Log.v(TAG, "Request to URL: " + url);

        JSONArray data = new JSONArray();

        try {
            for(EnergyUsageModel dModel : deviceModels)
                data.put(new JSONObject(mapper.writeValueAsString(dModel.getSerializeableDevice())));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequestTweaked request = new JsonArrayRequestTweaked(JsonRequest.Method.PUT, url, data.toString(), future, future);

        requestQueue.add(request);

        // Handle response synchronous.
        try {
            JSONArray response = future.get(); // this will block

            for (int i = 0; i < response.length(); i++) {
                errorModels.add(mapper.readValue(response.get(i).toString(), EnergyUsageModel.class));
            }

            return errorModels;
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

            e.printStackTrace();
        }

        Log.e(TAG, "Error from server!!");

        return null;
    }
}
