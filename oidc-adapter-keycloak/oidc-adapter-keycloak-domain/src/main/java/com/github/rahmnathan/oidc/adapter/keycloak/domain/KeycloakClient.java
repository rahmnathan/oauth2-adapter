package com.github.rahmnathan.oidc.adapter.keycloak.domain;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.KeycloakConfiguration;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.exception.TokenProviderException;
import com.nimbusds.jwt.SignedJWT;

public interface KeycloakClient {
    SignedJWT getAccessToken(String requestBody) throws TokenProviderException;
    void init(KeycloakConfiguration config);
}
