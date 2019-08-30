package com.github.rahmnathan.oauth2.adapter.keycloak.resilience4j;

import com.github.rahmnathan.oauth2.adapter.domain.OAuth2Service;
import com.github.rahmnathan.oauth2.adapter.domain.client.OAuth2ClientConfig;
import com.github.rahmnathan.oauth2.adapter.domain.credential.Duration;
import com.github.rahmnathan.oauth2.adapter.domain.credential.OAuth2CredentialJwt;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;

public class KeycloakClientResiliency4jIT {
    private final String url = "http://localhost:8080/auth";
    private OAuth2Service oAuth2Service;

    public KeycloakClientResiliency4jIT() {
        OAuth2ClientConfig clientConfig = OAuth2ClientConfig.builder()
                .url(url)
                .initialRetryDelay(400)
                .retryCount(2)
                .timoutMs(4000)
                .build();

        OAuth2CredentialJwt jwtConfig = OAuth2CredentialJwt.builder()
                .keyAlg("RSA")
                .privateKey("")
                .ttl(new Duration(ChronoUnit.SECONDS, 30))
                .url(url)
                .tokenRefreshThreshold(new Duration(ChronoUnit.SECONDS, 30))
                .clientId("movieuser")
                .realm("LocalMovies")
                .build();

        KeycloakClientResilience4j clientResilience4j = new KeycloakClientResilience4j(clientConfig);
        this.oAuth2Service = new OAuth2Service(jwtConfig, clientResilience4j);
    }

    @Test
    public void getAccessTokenTest() throws Exception {
        SignedJWT signedJWT = oAuth2Service.getAccessToken();

        System.out.println(signedJWT);
    }
}
