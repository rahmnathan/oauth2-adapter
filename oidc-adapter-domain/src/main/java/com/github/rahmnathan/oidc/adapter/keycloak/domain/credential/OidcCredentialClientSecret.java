package com.github.rahmnathan.oidc.adapter.keycloak.domain.credential;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.providers.AuthType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class OidcCredentialClientSecret extends OidcCredential {
    @NonNull
    private final String clientSecret;

    @Builder
    public OidcCredentialClientSecret(String realm, String clientId, Duration tokenRefreshThreshold, @NonNull String clientSecret) {
        super(realm, clientId, tokenRefreshThreshold);
        this.clientSecret = clientSecret;
    }

    @Override
    public AuthType getAuthType(){
        return AuthType.CLIENT_SECRET;
    }

    @Override
    public String toRequestBody() {
        return null;
    }
}
