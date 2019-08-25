package com.github.rahmnathan.oidc.adapter.keycloak.domain.config;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.providers.AuthType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
public abstract class KeycloakRequestConfig {
    private final String realm;
    private final String clientId;
    private final TokenRefreshThreshold tokenRefreshThreshold;

    public abstract AuthType getAuthType();

    @Value
    public static class TokenRefreshThreshold {
        private final ChronoUnit unit;
        private final long value;
    }
}
