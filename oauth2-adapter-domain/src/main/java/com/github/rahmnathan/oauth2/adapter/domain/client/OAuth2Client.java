package com.github.rahmnathan.oauth2.adapter.domain.client;

import com.github.rahmnathan.oauth2.adapter.domain.exception.OAuth2AdapterException;
import com.nimbusds.jwt.SignedJWT;

public abstract class OAuth2Client {
    protected final OAuth2ClientConfig config;

    public OAuth2Client(OAuth2ClientConfig config) {
        this.config = config;
    }

    public abstract SignedJWT getAccessToken(String requestBody, String realm) throws OAuth2AdapterException;
}
