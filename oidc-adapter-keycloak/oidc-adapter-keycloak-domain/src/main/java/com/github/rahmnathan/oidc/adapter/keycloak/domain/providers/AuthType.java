package com.github.rahmnathan.oidc.adapter.keycloak.domain.providers;

import com.github.rahmnathan.oidc.adapter.keycloak.domain.RequestBodyProvider;

public enum AuthType {
    JWT(new RequestBodyProviderJwt()),
    PASSWORD(new RequestBodyProviderPassword()),
    CLIENT_SECRET(null);

    private final RequestBodyProvider requestBodyProvider;

    public RequestBodyProvider getRequestBodyProvider(){
        return requestBodyProvider;
    }

    AuthType(RequestBodyProvider requestBodyProvider){
        this.requestBodyProvider = requestBodyProvider;
    }
}
