package com.github.rahmnathan.oidc.adapter.keycloak.domain.client;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.exception.OidcAdapterException;
import com.nimbusds.jwt.SignedJWT;

public abstract class OidcClient {
    protected final OidcClientConfig config;

    public OidcClient(OidcClientConfig config) {
        this.config = config;
    }

    public abstract SignedJWT getAccessToken(String requestBody, String realm) throws OidcAdapterException;
}
