package com.github.rahmnathan.oidc.adapter.keycloak.domain.exception;

public class OidcAdapterException extends Exception {
    public OidcAdapterException(Throwable e){
        super(e);
    }
    public OidcAdapterException(String message) {
        super(message);
    }
}
