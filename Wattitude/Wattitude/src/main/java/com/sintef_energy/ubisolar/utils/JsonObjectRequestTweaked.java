/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Håvard on 05.05.14.
 *
 * Handle JsonObjectRequest with HTTP code in the 200s and no payload as successful
 */
public class JsonObjectRequestTweaked extends JsonObjectRequest {

    public JsonObjectRequestTweaked(int method, String url, JSONObject requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            if (jsonString.length() > 0)
                return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
            else
                throw new JSONException("String has no length");

        } catch (UnsupportedEncodingException | JSONException | NullPointerException e) {
            if (response.statusCode >= 200 && response.statusCode < 300)
                //Call was still successful we just got no body in the response
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
            else
                return Response.error(new ParseError(e));
        }
    }
}
