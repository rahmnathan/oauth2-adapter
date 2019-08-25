package com.github.rahmnathan.oidc.adapter.keycloak.domain.config;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.providers.AuthType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class ClientSecretConfig extends KeycloakRequestConfig {
    @NonNull
    private final String clientSecret;

    @Builder
    public ClientSecretConfig(String realm, String clientId, @NonNull String clientSecret) {
        super(realm, clientId);
        this.clientSecret = clientSecret;
    }

    @Override
    public AuthType getAuthType(){
        return AuthType.CLIENT_SECRET;
    }
}
