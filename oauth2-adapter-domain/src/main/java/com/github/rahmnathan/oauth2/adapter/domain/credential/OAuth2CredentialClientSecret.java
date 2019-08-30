package com.github.rahmnathan.oauth2.adapter.domain.credential;

import com.github.rahmnathan.oauth2.adapter.domain.providers.AuthType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class OAuth2CredentialClientSecret extends OAuth2Credential {
    @NonNull
    private final String clientSecret;

    @Builder
    public OAuth2CredentialClientSecret(String realm, String clientId, Duration tokenRefreshThreshold, @NonNull String clientSecret) {
        super(realm, clientId, tokenRefreshThreshold);
        this.clientSecret = clientSecret;
    }

    @Override
    public AuthType getAuthType(){
        return AuthType.CLIENT_SECRET;
    }

    @Override
    public String toRequestBody() {
        return null;
    }
}
