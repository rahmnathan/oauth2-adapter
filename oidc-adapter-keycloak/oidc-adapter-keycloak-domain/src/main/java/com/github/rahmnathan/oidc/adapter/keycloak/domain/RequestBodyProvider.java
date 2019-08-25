package com.github.rahmnathan.oidc.adapter.keycloak.domain;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.KeycloakRequestConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.exception.TokenProviderException;

public interface RequestBodyProvider {
    String buildRequestBody(KeycloakRequestConfig config) throws TokenProviderException;
}
