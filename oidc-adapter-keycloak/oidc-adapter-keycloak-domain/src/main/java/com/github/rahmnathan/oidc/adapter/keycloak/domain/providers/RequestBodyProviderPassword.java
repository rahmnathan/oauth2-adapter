package com.github.rahmnathan.oidc.adapter.keycloak.domain.providers;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.RequestBodyProvider;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.KeycloakConfiguration;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.exception.TokenProviderException;

import java.net.URLEncoder;

public class RequestBodyProviderPassword implements RequestBodyProvider {

    @Override
    public String buildRequestBody(KeycloakConfiguration configuration) throws TokenProviderException {
        KeycloakConfiguration.Password passwordConfig = configuration.getPassword();
        if(passwordConfig == null){
            throw new TokenProviderException("Password configuration cannot be null.");
        }

        return "client_id=" + URLEncoder.encode(configuration.getClientId()) +
                "&grant_type=password" +
                "&username=" + URLEncoder.encode(passwordConfig.getUsername()) +
                "&password=" + URLEncoder.encode(passwordConfig.getPassword());
    }
}
