package com.github.rahmnathan.oidc.adapter.keycloak.domain.exception;

public class TokenProviderException extends Exception {
    public TokenProviderException(Throwable e){
        super(e);
    }
    public TokenProviderException(String message) {
        super(message);
    }
}
