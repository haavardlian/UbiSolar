package com.sintef_energy.ubisolar.presenter;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * Created by Lars Erik on 01.05.2014.
 */
public class RequestFriendsProxy {

    private RequestQueue requestQueue;
    private ObjectMapper mapper;

    private Context context;

    // package access constructor
    RequestFriendsProxy(Context context, RequestQueue requestQueue) {
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        this.context = context;
        this.requestQueue = requestQueue;
    }
}
