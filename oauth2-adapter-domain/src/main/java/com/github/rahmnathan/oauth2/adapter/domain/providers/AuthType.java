package com.github.rahmnathan.oauth2.adapter.domain.providers;

import com.github.rahmnathan.oauth2.adapter.domain.credential.OAuth2Credential;
import com.github.rahmnathan.oauth2.adapter.domain.credential.OAuth2CredentialClientSecret;
import com.github.rahmnathan.oauth2.adapter.domain.credential.OAuth2CredentialJwt;
import com.github.rahmnathan.oauth2.adapter.domain.credential.OAuth2CredentialPassword;
import lombok.Getter;

@Getter
public enum AuthType {
    JWT(OAuth2CredentialJwt.class),
    PASSWORD(OAuth2CredentialPassword.class),
    CLIENT_SECRET(OAuth2CredentialClientSecret.class);

    private final Class<? extends OAuth2Credential> configurationType;

    AuthType(Class<? extends OAuth2Credential> configurationType){
        this.configurationType = configurationType;
    }
}
