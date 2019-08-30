package com.github.rahmnathan.oauth2.adapter.domain.credential;

import com.github.rahmnathan.oauth2.adapter.domain.providers.AuthType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.net.URLEncoder;

@EqualsAndHashCode(callSuper = true)
@Value
public class OAuth2CredentialPassword extends OAuth2Credential {
    @NonNull
    private final String username;
    @NonNull
    private final String password;

    @Builder
    public OAuth2CredentialPassword(String realm, String clientId, Duration tokenRefreshThreshold,
                                  @NonNull String username, @NonNull String password) {
        super(realm, clientId, tokenRefreshThreshold);
        this.username = username;
        this.password = password;
    }

    @Override
    public AuthType getAuthType(){
        return AuthType.PASSWORD;
    }

    @Override
    public String toRequestBody(){
        return "client_id=" + URLEncoder.encode(getClientId()) +
                "&grant_type=password" +
                "&username=" + URLEncoder.encode(username) +
                "&password=" + URLEncoder.encode(password);
    }
}
