package com.sintef_energy.ubisolar.utils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Håvard on 05.05.14.
 */
public class JsonObjectRequestTweaked extends JsonObjectRequest {

    public JsonObjectRequestTweaked(int method, String url, JSONObject requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            if(jsonString.length() > 0)
                return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
            else
                return Response.error(new ParseError(response));
        } catch (UnsupportedEncodingException | JSONException | NullPointerException e) {
            return Response.error(new ParseError(e));
        }
    }

}
