package com.github.rahmnathan.oauth2.adapter.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.rahmnathan.oauth2.adapter.domain.exception.OAuth2AdapterException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

public class AccessTokenResponseParser {
    public static final String ACCESS_TOKEN_FIELD_NAME = "access_token";

    public SignedJWT extractAccessToken(JsonNode node) throws OAuth2AdapterException {
        if (node != null && node.hasNonNull(ACCESS_TOKEN_FIELD_NAME)) {
            String[] jwtParts = node.get(ACCESS_TOKEN_FIELD_NAME).asText().split("\\.");
            if (jwtParts.length != 3) {
                throw new OAuth2AdapterException("Malformed access_token in response.");
            }

            try {
                return new SignedJWT(new Base64URL(jwtParts[0]), new Base64URL(jwtParts[1]), new Base64URL(jwtParts[2]));
            } catch (ParseException e){
                throw new OAuth2AdapterException(e);
            }
        }

        throw new OAuth2AdapterException("No access_token present in response.");
    }
}
