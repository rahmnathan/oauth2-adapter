package com.github.rahmnathan.oidc.adapter.keycloak.domain.config;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.providers.AuthType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class PasswordConfig extends KeycloakRequestConfig {
    @NonNull
    private final String username;
    @NonNull
    private final String password;

    @Builder
    public PasswordConfig(String realm, String clientId, TokenRefreshThreshold tokenRefreshThreshold,
                          @NonNull String username, @NonNull String password) {
        super(realm, clientId, tokenRefreshThreshold);
        this.username = username;
        this.password = password;
    }

    @Override
    public AuthType getAuthType(){
        return AuthType.PASSWORD;
    }
}
