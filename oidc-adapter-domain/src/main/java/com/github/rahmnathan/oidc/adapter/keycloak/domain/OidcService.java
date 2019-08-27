package com.github.rahmnathan.oidc.adapter.keycloak.domain;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.client.OidcClient;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.credential.Duration;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.credential.OidcCredential;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;

public class OidcService {
    private final Logger logger = LoggerFactory.getLogger(OidcService.class);
    private final OidcCredential config;
    private final OidcClient client;
    private SignedJWT cachedToken;

    public OidcService(OidcCredential configuration, OidcClient client) {
        this.client = client;
        this.config = configuration;
    }

    public SignedJWT getAccessToken() throws Exception {
        logger.info("Attempting to acquire access token");

        if(cachedToken != null){
            Duration refreshThreshold = config.getTokenRefreshThreshold();
            Date compareDate = Date.from(Instant.now().plus(refreshThreshold.getValue(), refreshThreshold.getUnit()));
            if(cachedToken.getJWTClaimsSet().getExpirationTime().after(compareDate)){
                logger.debug("Using cached access token.");
                return cachedToken;
            }
        }

        cachedToken = client.getAccessToken(config.toRequestBody(), config.getRealm());
        return cachedToken;
    }
}
