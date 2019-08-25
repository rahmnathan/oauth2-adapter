package com.github.rahmnathan.oidc.adapter.keycloak.domain;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.client.KeycloakClient;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.KeycloakRequestConfig;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;

import static java.time.temporal.ChronoUnit.SECONDS;

public class KeycloakService {
    private final Logger logger = LoggerFactory.getLogger(KeycloakService.class);
    private final KeycloakRequestConfig config;
    private final KeycloakClient client;
    private SignedJWT cachedToken;

    public KeycloakService(KeycloakRequestConfig configuration, KeycloakClient client) {
        this.client = client;
        this.config = configuration;
    }

    public SignedJWT getAccessToken() throws Exception {
        logger.info("Attempting to acquire access token");

        if(cachedToken != null){
            Date compareDate = Date.from(Instant.now().plus(30, SECONDS));
            if(cachedToken.getJWTClaimsSet().getExpirationTime().after(compareDate)){
                logger.debug("Using cached access token.");
                return cachedToken;
            }
        }

        RequestBodyProvider requestBodyProvider = config.getAuthType().getBodyProvider();
        Class<? extends KeycloakRequestConfig> requestConfigType = config.getAuthType().getConfigurationType();

        String requestBody = requestBodyProvider.buildRequestBody(requestConfigType.cast(config));
        cachedToken = client.getAccessToken(requestBody, config.getRealm());

        return cachedToken;
    }
}
