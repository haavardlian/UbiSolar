package com.sintef_energy.ubisolar.structs;

/**
 * Created by Håvard on 05.03.14.
 */
public class SimpleJSONMessage {
    private String message;

    public SimpleJSONMessage(String message) {
        this.message = message;
    }

    public SimpleJSONMessage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
