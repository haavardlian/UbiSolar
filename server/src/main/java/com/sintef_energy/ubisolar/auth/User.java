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

import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.UserIdentity;

/**
 * Created by Håvard on 26.03.14.
 */

public class User implements Authentication.User {

    private String token;

    public User(String token) {
        this.token = token;
    }

    public User() {
    }

    public String getToken() { return token; }

    @Override
    public String getAuthMethod() {
        return null;
    }

    @Override
    public UserIdentity getUserIdentity() {
        return null;
    }

    @Override
    public boolean isUserInRole(UserIdentity.Scope scope, String s) {
        return false;
    }

    @Override
    public void logout() {

    }
}
