/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
