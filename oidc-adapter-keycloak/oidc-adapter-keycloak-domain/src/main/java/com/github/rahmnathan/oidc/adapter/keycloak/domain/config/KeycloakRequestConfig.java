package com.github.rahmnathan.oidc.adapter.keycloak.domain.config;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.providers.AuthType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class KeycloakRequestConfig {
    private final String realm;
    private final String clientId;

    public abstract AuthType getAuthType();
}
