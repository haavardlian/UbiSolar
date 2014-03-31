package com.sintef_energy.ubisolar.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ResponseDelivery;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.adapter.TipAdapter;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.model.TipRating;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * Created by Håvard on 25.03.2014.
 */
public class RequestProxy {

    private RequestQueue requestQueue;
    private Activity activity;
    private ObjectMapper mapper;
    // package access constructor
    RequestProxy(Activity activity) {
        requestQueue = newRequestQueue(activity.getApplicationContext());
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        this.activity = activity;
    }

    public void getAllTips(final TipAdapter adapter)
    {
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
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        activity.setProgressBarIndeterminateVisibility(false);
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.setProgressBarIndeterminateVisibility(false);
                        Toast.makeText(activity, "Could not get data from server",
                                Toast.LENGTH_LONG).show();
                        Log.e("REQUEST", "Error from server!!");
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
                                    Toast.makeText(activity, response.getString("message"),
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
                                Toast.makeText(activity, "An error occurred",
                                        Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(activity, response.getString("message"),
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
                                Toast.makeText(activity, "An error occurred",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        requestQueue.add(jsonRequest);
    }

    public static RequestQueue newRequestQueue(Context context) {
        File cacheDir = new File(context.getCacheDir(), "def_cahce_dir");
        HttpStack stack;
        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 9) {
            stack = new HurlStack();
        } else {
            // Prior to Gingerbread, HttpUrlConnection was unreliable.
            // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
            stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
        }


        Network network = new BasicNetwork(stack);
        int threadPoolSize = 10; // number of network dispatcher threads to create
        // pass Executor to constructor of ResponseDelivery object
        ResponseDelivery delivery = new ExecutorDelivery(Executors.newFixedThreadPool(threadPoolSize));
        // pass ResponseDelivery object as a 4th parameter for RequestQueue constructor
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network, threadPoolSize, delivery);
        queue.start();

        return queue;
    }

}
