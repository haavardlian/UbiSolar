package com.sintef_energy.ubisolar.structs;

/**
 * Created by Håvard on 26.03.14.
 */
public class SimpleToken {
    String token;

    public SimpleToken(String token) {
        this.token = token;
    }

    public SimpleToken() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
