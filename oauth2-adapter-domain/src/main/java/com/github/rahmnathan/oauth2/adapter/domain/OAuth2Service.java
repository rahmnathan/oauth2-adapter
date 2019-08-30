package com.github.rahmnathan.oauth2.adapter.domain;

import com.github.rahmnathan.oauth2.adapter.domain.client.OAuth2Client;
import com.github.rahmnathan.oauth2.adapter.domain.credential.Duration;
import com.github.rahmnathan.oauth2.adapter.domain.credential.OAuth2Credential;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;

public class OAuth2Service {
    private final Logger logger = LoggerFactory.getLogger(OAuth2Service.class);
    private final OAuth2Credential config;
    private final OAuth2Client client;
    private SignedJWT cachedToken;

    public OAuth2Service(OAuth2Credential configuration, OAuth2Client client) {
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
