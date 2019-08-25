package com.github.rahmnathan.oidc.adapter.keycloak.domain;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.KeycloakConfiguration;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.exception.TokenProviderException;

public interface RequestBodyProvider {
    String buildRequestBody(KeycloakConfiguration configuration) throws TokenProviderException;
}
