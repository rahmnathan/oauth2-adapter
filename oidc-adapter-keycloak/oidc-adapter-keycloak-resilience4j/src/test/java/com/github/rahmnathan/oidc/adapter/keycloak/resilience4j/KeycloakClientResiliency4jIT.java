package com.github.rahmnathan.oidc.adapter.keycloak.resilience4j;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.KeycloakService;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.client.KeycloakClientConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.JwtConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.KeycloakRequestConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.PasswordConfig;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;

public class KeycloakClientResiliency4jIT {
    private final String url = "http://localhost:8080/auth";
    private KeycloakService keycloakService;

    public KeycloakClientResiliency4jIT() {
        KeycloakClientConfig clientConfig = KeycloakClientConfig.builder()
                .url(url)
                .initialRetryDelay(400)
                .retryCount(2)
                .timoutMs(4000)
                .build();

        JwtConfig jwtConfig = JwtConfig.builder()
                .keyAlg("RSA")
                .privateKey("")
                .ttlUnit(ChronoUnit.SECONDS)
                .ttlValue(30)
                .url(url)
                .tokenRefreshThreshold(new KeycloakRequestConfig.TokenRefreshThreshold(ChronoUnit.SECONDS, 30))
                .clientId("movieuser")
                .realm("LocalMovies")
                .build();;

        KeycloakClientResilience4j clientResilience4j = new KeycloakClientResilience4j(clientConfig);
        this.keycloakService = new KeycloakService(jwtConfig, clientResilience4j);
    }

    @Test
    public void getAccessTokenTest() throws Exception {
        SignedJWT signedJWT = keycloakService.getAccessToken();

        System.out.println(signedJWT);
    }
}
