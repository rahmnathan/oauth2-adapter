package com.github.rahmnathan.oidc.adapter.keycloak.domain.client;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.exception.TokenProviderException;
import com.nimbusds.jwt.SignedJWT;

public abstract class KeycloakClient {
    protected final KeycloakClientConfig config;

    public KeycloakClient(KeycloakClientConfig config) {
        this.config = config;
    }

    public abstract SignedJWT getAccessToken(String requestBody, String realm) throws TokenProviderException;
}
