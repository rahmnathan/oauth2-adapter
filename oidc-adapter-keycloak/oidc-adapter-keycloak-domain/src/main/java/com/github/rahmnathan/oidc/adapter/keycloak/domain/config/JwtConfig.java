package com.github.rahmnathan.oidc.adapter.keycloak.domain.config;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.providers.AuthType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@EqualsAndHashCode(callSuper = true)
@Value
public class JwtConfig extends KeycloakRequestConfig {

    @NonNull
    private final String url;
    @NonNull
    private final String privateKey;
    @NonNull
    private final String keyAlg;
    @NonNull
    private final ChronoUnit ttlUnit;
    @NonNull
    private final long ttlValue;

    @Builder
    public JwtConfig(String realm, String clientId, @NonNull String url, @NonNull String privateKey, @NonNull String keyAlg, @NonNull ChronoUnit ttlUnit, @NonNull long ttlValue) {
        super(realm, clientId);
        this.url = url;
        this.privateKey = privateKey;
        this.keyAlg = keyAlg;
        this.ttlUnit = ttlUnit;
        this.ttlValue = ttlValue;
    }

    public PrivateKey buildPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedKey = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory kf = KeyFactory.getInstance(keyAlg);
        return kf.generatePrivate(keySpec);
    }

    @Override
    public AuthType getAuthType(){
        return AuthType.JWT;
    }
}
