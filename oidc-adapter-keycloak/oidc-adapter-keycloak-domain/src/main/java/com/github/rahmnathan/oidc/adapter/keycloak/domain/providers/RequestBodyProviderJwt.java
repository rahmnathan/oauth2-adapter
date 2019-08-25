package com.github.rahmnathan.oidc.adapter.keycloak.domain.providers;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.RequestBodyProvider;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.JwtConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.client.KeycloakClientConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.KeycloakRequestConfig;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.exception.TokenProviderException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class RequestBodyProviderJwt implements RequestBodyProvider {

    public String buildRequestBody(KeycloakRequestConfig config) throws TokenProviderException {
        if(config.getAuthType() != AuthType.JWT){
            throw new TokenProviderException("Invalid JWT configuration.");
        }

        JwtConfig jwtConfig = (JwtConfig) config;

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .audience(jwtConfig.getUrl() + "/realms/" + jwtConfig.getRealm())
                .expirationTime(Date.from(Instant.now().plus(jwtConfig.getTtlValue(), jwtConfig.getTtlUnit())))
                .subject(jwtConfig.getClientId())
                .notBeforeTime(Date.from(Instant.now()))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS512), claimsSet);

        try {
            signedJWT.sign(new RSASSASigner(jwtConfig.buildPrivateKey()));
        } catch (JOSEException | InvalidKeySpecException | NoSuchAlgorithmException e){
            throw new TokenProviderException(e);
        }

        return  "client_id=" + URLEncoder.encode(jwtConfig.getClientId()) +
                "&grant_type=client_credentials" +
                "&client_assertion_type=urn:ietf:params:oauth:client-assertion-type:jwt-bearer" +
                "&client_assertion=" + signedJWT.serialize();
    }
}
