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

package com.sintef_energy.ubisolar.auth;

import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Håvard on 26.03.14.
 */
public class Auth implements Authenticator<String, User> {

    @Override
    public Optional<User> authenticate(String token) throws AuthenticationException {
        String json = makeJsonRequest(token);
        if(!json.regionMatches(true, 0, "{\"error\":", 0, 9)) {
            return Optional.of(new User(token));
        }
        return null;
    }

    private String makeJsonRequest(String token) {
            URL url;
            HttpURLConnection conn;
            BufferedReader rd;
            String line;
            String result = "";
            try {
                url = new URL("http://graph.facebook.com/me?token=" + token);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = rd.readLine()) != null) {
                    result += line;
                }
                rd.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(result);
            return result;
    }
}
