package com.github.rahmnathan.oauth2.adapter.domain.credential;

import com.github.rahmnathan.oauth2.adapter.domain.exception.OAuth2AdapterException;
import com.github.rahmnathan.oauth2.adapter.domain.key.SignerInstructions;
import com.github.rahmnathan.oauth2.adapter.domain.providers.AuthType;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Value
public class OAuth2CredentialJwt extends OAuth2Credential {

    @NonNull
    private final String url;
    @NonNull
    private final String privateKey;
    @NonNull
    private final String keyAlg;
    @NonNull
    private final Duration ttl;

    @Builder
    public OAuth2CredentialJwt(String realm, String clientId, Duration tokenRefreshThreshold, @NonNull String url,
                             @NonNull String privateKey, @NonNull String keyAlg, @NonNull Duration ttl) {
        super(realm, clientId, tokenRefreshThreshold);
        this.url = url;
        this.privateKey = privateKey;
        this.keyAlg = keyAlg;
        this.ttl = ttl;
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

    @Override
    public String toRequestBody() throws OAuth2AdapterException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .audience(url + "/realms/" + getRealm())
                .expirationTime(Date.from(Instant.now().plus(ttl.getValue(), ttl.getUnit())))
                .subject(getClientId())
                .notBeforeTime(Date.from(Instant.now()))
                .build();

        try {
            SignerInstructions signerInstructions = SignerInstructions.fromKey(buildPrivateKey());
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(signerInstructions.getJwsAlgorithm()), claimsSet);
            signedJWT.sign(signerInstructions.getJwsSigner());

            return  "client_id=" + URLEncoder.encode(getClientId()) +
                    "&grant_type=client_credentials" +
                    "&client_assertion_type=urn:ietf:params:oauth:client-assertion-type:jwt-bearer" +
                    "&client_assertion=" + signedJWT.serialize();
        } catch (JOSEException | InvalidKeySpecException | NoSuchAlgorithmException e){
            throw new OAuth2AdapterException(e);
        }
    }
}
