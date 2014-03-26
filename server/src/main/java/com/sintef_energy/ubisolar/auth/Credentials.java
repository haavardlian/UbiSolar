package com.sintef_energy.ubisolar.auth;

import com.sun.javafx.fxml.expression.Expression;
import com.yammer.dropwizard.auth.basic.BasicCredentials;
import org.scribe.model.Token;

/**
 * Created by Håvard on 26.03.14.
 */
public class Credentials extends BasicCredentials {

    private Token token;

    public Credentials(String username, String password, String token, String secret) {
        super(username, password);
        this.token = new Token(token, secret);
    }

    public Token getToken() { return token; }
}
