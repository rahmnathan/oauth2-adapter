package com.github.rahmnathan.oidc.adapter.keycloak.domain.providers;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.credential.OidcCredentialClientSecret;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.credential.OidcCredentialJwt;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.credential.OidcCredential;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.credential.OidcCredentialPassword;
import lombok.Getter;

@Getter
public enum AuthType {
    JWT(OidcCredentialJwt.class),
    PASSWORD(OidcCredentialPassword.class),
    CLIENT_SECRET(OidcCredentialClientSecret.class);

    private final Class<? extends OidcCredential> configurationType;

    AuthType(Class<? extends OidcCredential> configurationType){
        this.configurationType = configurationType;
    }
}
