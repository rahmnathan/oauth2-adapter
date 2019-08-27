package com.github.rahmnathan.oidc.adapter.keycloak.camel;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.rahmnathan.oidc.adapter.keycloak.domain.exception.OidcAdapterException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

public class KeycloakTokenResponseParser {
    public static final String ACCESS_TOKEN_FIELD_NAME = "access_token";

    public SignedJWT extractAccessToken(JsonNode node) throws OidcAdapterException {
        if (node != null && node.hasNonNull(ACCESS_TOKEN_FIELD_NAME)) {
            String[] jwtParts = node.get(ACCESS_TOKEN_FIELD_NAME).asText().split("\\.");
            if (jwtParts.length != 3) {
                throw new OidcAdapterException("Malformed AccessToken in Keycloak response.");
            }

            try {
                return new SignedJWT(new Base64URL(jwtParts[0]), new Base64URL(jwtParts[1]), new Base64URL(jwtParts[2]));
            } catch (ParseException e){
                throw new OidcAdapterException(e);
            }
        }

        throw new OidcAdapterException("No AccessToken present in Keycloak response.");
    }
}
