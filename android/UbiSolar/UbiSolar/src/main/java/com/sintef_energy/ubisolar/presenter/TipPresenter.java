package com.sintef_energy.ubisolar.presenter;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.deser.std.JacksonDeserializers;
import com.sintef_energy.ubisolar.structs.Device;
import com.sintef_energy.ubisolar.structs.Tip;
import com.sintef_energy.ubisolar.utils.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Håvard on 19.03.14.
 */
public class TipPresenter {
    String tag = "SERVER";
    RequestQueue requestQueue;

    public TipPresenter(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void getAllTips(final ArrayAdapter<Tip> adapter, final ArrayList<Tip> tipArrayList)
    {
        String url = Global.BASE_URL + "/tips";

        JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
                try {
                    List<Tip> tips = mapper.readValue(jsonArray.toString(), new TypeReference<List<Tip>>(){});
                    tipArrayList.clear();
                    for(Tip tip : tips) {
                        tipArrayList.add(tip);
                        Log.v(tag, tip.toString());
                    }
                    adapter.notifyDataSetChanged();
                } catch (IOException e) {
                    Log.e(tag, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(tag, error.toString());
                //TODO display error
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonRequest);
    }
}
