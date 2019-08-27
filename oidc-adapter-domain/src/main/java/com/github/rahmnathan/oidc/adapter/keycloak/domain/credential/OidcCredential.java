package com.github.rahmnathan.oidc.adapter.keycloak.domain.credential;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.exception.OidcAdapterException;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.providers.AuthType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class OidcCredential {
    private final String realm;
    private final String clientId;
    private final Duration tokenRefreshThreshold;

    public abstract AuthType getAuthType();
    public abstract String toRequestBody() throws OidcAdapterException;
}
