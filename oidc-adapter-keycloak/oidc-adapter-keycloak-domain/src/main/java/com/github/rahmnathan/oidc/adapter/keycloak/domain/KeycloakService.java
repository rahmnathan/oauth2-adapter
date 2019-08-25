package com.github.rahmnathan.oidc.adapter.keycloak.domain;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.KeycloakConfiguration;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;

import static java.time.temporal.ChronoUnit.SECONDS;

public class KeycloakService {
    private final Logger logger = LoggerFactory.getLogger(KeycloakService.class);
    private RequestBodyProvider requestBodyProvider;
    private final KeycloakConfiguration configuration;
    private final KeycloakClient client;
    private SignedJWT cachedToken;

    public KeycloakService(KeycloakConfiguration configuration, KeycloakClient client, AuthType authType) {
        this.requestBodyProvider = authType.getRequestBodyProvider();
        this.configuration = configuration;
        this.client = client;
        client.init(configuration);
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

        String requestBody = requestBodyProvider.buildRequestBody(configuration);
        cachedToken = client.getAccessToken(requestBody);
        return cachedToken;
    }
}
