package com.github.rahmnathan.oidc.adapter.keycloak.domain.providers;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.RequestBodyProvider;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.config.KeycloakConfiguration;
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

    @Override
    public String buildRequestBody(KeycloakConfiguration configuration) throws TokenProviderException {
        KeycloakConfiguration.Jwt jwtConfig = configuration.getJwt();
        if(jwtConfig == null){
            throw new TokenProviderException("JWT configuration cannot be null.");
        }

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .audience(configuration.getUrl() + "/realms/" + configuration.getRealm())
                .expirationTime(Date.from(Instant.now().plus(jwtConfig.getTtlValue(), jwtConfig.getTtlUnit())))
                .subject(configuration.getClientId())
                .notBeforeTime(Date.from(Instant.now()))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS512), claimsSet);

        try {
            signedJWT.sign(new RSASSASigner(jwtConfig.buildPrivateKey()));
        } catch (JOSEException | InvalidKeySpecException | NoSuchAlgorithmException e){
            throw new TokenProviderException(e);
        }

        return  "client_id=" + URLEncoder.encode(configuration.getClientId()) +
                "&grant_type=client_credentials" +
                "&client_assertion_type=urn:ietf:params:oauth:client-assertion-type:jwt-bearer" +
                "&client_assertion=" + signedJWT.serialize();
    }
}
