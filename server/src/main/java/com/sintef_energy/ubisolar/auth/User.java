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
