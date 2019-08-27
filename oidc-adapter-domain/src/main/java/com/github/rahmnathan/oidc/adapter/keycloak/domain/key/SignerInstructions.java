package com.github.rahmnathan.oidc.adapter.keycloak.domain.key;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.exception.OidcAdapterException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;

@Getter
@AllArgsConstructor
public class SignerInstructions {
    private final JWSAlgorithm jwsAlgorithm;
    private final JWSSigner jwsSigner;

    public static SignerInstructions fromKey(PrivateKey key) throws OidcAdapterException {
        for(SigningStrategy signingStrategy : SigningStrategy.values()){
            if(signingStrategy.key.isAssignableFrom(key.getClass())){
                return signingStrategy.instantiate(key);
            }
        }

        throw new OidcAdapterException("Failed to find Signer for key.");
    }

    @Getter
    @AllArgsConstructor
    enum SigningStrategy {
        RSA(RSAPrivateKey.class, JWSAlgorithm.RS512, RSASSASigner::new),
        EC(ECPrivateKey.class, JWSAlgorithm.ES512, key-> new ECDSASigner((ECPrivateKey) key));

        private final SignerConstructor<PrivateKey, JWSSigner> constructor;
        private final Class<? extends PrivateKey> key;
        private final JWSAlgorithm jwsAlgorithm;

        SigningStrategy(Class<? extends PrivateKey> key, JWSAlgorithm jwsAlgorithm, SignerConstructor<PrivateKey, JWSSigner> constructor) {
            this.key = key;
            this.jwsAlgorithm = jwsAlgorithm;
            this.constructor = constructor;
        }

        public SignerInstructions instantiate(PrivateKey privateKey) throws OidcAdapterException {
            try {
                return new SignerInstructions(jwsAlgorithm, constructor.apply(privateKey));
            } catch (JOSEException e){
                throw new OidcAdapterException(e);
            }
        }

        @FunctionalInterface
        interface SignerConstructor<T, R> {
            R apply(T obj) throws JOSEException;
        }
    }
}
