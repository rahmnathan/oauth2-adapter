package com.github.rahmnathan.oidc.adapter.keycloak.domain.providers;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.RequestBodyProvider;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.KeycloakRequestConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.PasswordConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.exception.TokenProviderException;

import java.net.URLEncoder;

public class RequestBodyProviderPassword implements RequestBodyProvider {

    public String buildRequestBody(KeycloakRequestConfig config) throws TokenProviderException {
        if(!(config instanceof PasswordConfig)){
            throw new TokenProviderException("Invalid Password configuration.");
        }

        PasswordConfig passwordConfig = (PasswordConfig) config;

        return "client_id=" + URLEncoder.encode(passwordConfig.getClientId()) +
                "&grant_type=password" +
                "&username=" + URLEncoder.encode(passwordConfig.getUsername()) +
                "&password=" + URLEncoder.encode(passwordConfig.getPassword());
    }
}
