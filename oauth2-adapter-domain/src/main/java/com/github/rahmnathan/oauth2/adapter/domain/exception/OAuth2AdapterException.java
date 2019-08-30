package com.github.rahmnathan.oauth2.adapter.domain.exception;

public class OAuth2AdapterException extends Exception {
    public OAuth2AdapterException(Throwable e){
        super(e);
    }
    public OAuth2AdapterException(String message) {
        super(message);
    }
}
