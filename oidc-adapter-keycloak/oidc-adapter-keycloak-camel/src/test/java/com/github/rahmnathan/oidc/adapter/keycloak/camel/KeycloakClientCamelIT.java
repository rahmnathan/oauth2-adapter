package com.github.rahmnathan.oidc.adapter.keycloak.camel;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.client.KeycloakClientConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.JwtConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.KeycloakService;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.KeycloakRequestConfig;
import com.nimbusds.jwt.SignedJWT;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import java.time.temporal.ChronoUnit;

public class KeycloakClientCamelIT extends CamelTestSupport {
    private final String url = "http://localhost:8080/auth";
    private KeycloakClientCamel keycloakClientCamel;
    private KeycloakService keycloakService;

    @Before
    public void init() {
        KeycloakClientConfig clientConfig = KeycloakClientConfig.builder()
                .url(url)
                .initialRetryDelay(400)
                .retryCount(2)
                .timoutMs(4000)
                .build();

        this.keycloakClientCamel = KeycloakClientCamel.getInstance(template, context, clientConfig);

        JwtConfig jwtConfig = JwtConfig.builder()
                .keyAlg("RSA")
                .privateKey("")
                .ttlUnit(ChronoUnit.SECONDS)
                .ttlValue(30)
                .url(url)
                .tokenRefreshThreshold(new KeycloakRequestConfig.TokenRefreshThreshold(ChronoUnit.SECONDS, 30))
                .clientId("movieuser")
                .realm("LocalMovies")
                .build();

        this.keycloakService = new KeycloakService(jwtConfig, keycloakClientCamel);
    }

    @Test
    public void getAccessTokenTest() throws Exception {
        SignedJWT signedJWT = keycloakService.getAccessToken();

        System.out.println("blah");
    }
}
