package com.sintef_energy.ubisolar.utils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

/**
 * Created by perok on 30.04.14.
 *
 * Correctly handles no return data.
 */
public class JsonArrayRequestTweaked extends JsonRequest<JSONArray>{

    private static final String TAG = JsonArrayRequestTweaked.class.getName();

    public JsonArrayRequestTweaked(int method, String url, String requestBody, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            if(jsonString.length() > 0)
                return Response.success(new JSONArray(jsonString), HttpHeaderParser.parseCacheHeaders(response));
            else
                return Response.success(new JSONArray(), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException | JSONException | NullPointerException e) {
            return Response.error(new ParseError(e));
        }
    }
}
