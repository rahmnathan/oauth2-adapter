package com.github.rahmnathan.oidc.adapter.keycloak.domain;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.credential.OidcCredential;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.exception.OidcAdapterException;

public interface RequestBodyProvider {
    String buildRequestBody(OidcCredential config) throws OidcAdapterException;
}
