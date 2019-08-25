package com.github.rahmnathan.oidc.adapter.keycloak.domain.client;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class KeycloakClientConfig {
    @NonNull
    private final String url;
    private final int timoutMs;
    private final long initialRetryDelay;
    private final int retryCount;
}
