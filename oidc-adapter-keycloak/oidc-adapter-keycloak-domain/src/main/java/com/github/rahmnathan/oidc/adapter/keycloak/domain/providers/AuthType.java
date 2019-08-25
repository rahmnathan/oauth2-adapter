package com.github.rahmnathan.oidc.adapter.keycloak.domain.providers;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.RequestBodyProvider;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.ClientSecretConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.JwtConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.KeycloakRequestConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.PasswordConfig;
import lombok.Getter;

@Getter
public enum AuthType {
    JWT(JwtConfig.class, new RequestBodyProviderJwt()),
    PASSWORD(PasswordConfig.class, new RequestBodyProviderPassword()),
    CLIENT_SECRET(ClientSecretConfig.class, null);

    private final Class<? extends KeycloakRequestConfig> configurationType;
    private final RequestBodyProvider bodyProvider;

    AuthType(Class<? extends KeycloakRequestConfig> configurationType, RequestBodyProvider bodyProvider){
        this.configurationType = configurationType;
        this.bodyProvider = bodyProvider;
    }
}
