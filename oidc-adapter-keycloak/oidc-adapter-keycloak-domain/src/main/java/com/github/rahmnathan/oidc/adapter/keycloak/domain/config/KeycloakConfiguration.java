package com.github.rahmnathan.oidc.adapter.keycloak.domain.config;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Value;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Value
@Builder
public class KeycloakConfiguration {
    @NonNull
    private final String realm;
    @NonNull
    private final String clientId;
    @NonNull
    private final String url;

    private final Password password;
    private final Jwt jwt;
    private final ClientSecret clientSecret;

    @Data
    public static class Password {
        @NonNull
        private final String username;
        @NonNull
        private final String password;
    }

    @Data
    public static class Jwt {
        @NonNull
        private final String privateKey;
        @NonNull
        private final String keyAlg;

        public PrivateKey buildPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
            byte[] decodedKey = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory kf = KeyFactory.getInstance(keyAlg);
            return kf.generatePrivate(keySpec);
        }
    }

    @Data
    public static class ClientSecret {
        @NonNull
        private final String clientSecret;
    }
}
