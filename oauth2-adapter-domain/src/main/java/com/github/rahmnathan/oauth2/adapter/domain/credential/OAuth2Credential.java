package com.github.rahmnathan.oauth2.adapter.domain.credential;

import com.github.rahmnathan.oauth2.adapter.domain.exception.OAuth2AdapterException;
import com.github.rahmnathan.oauth2.adapter.domain.providers.AuthType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class OAuth2Credential {
    private final String realm;
    private final String clientId;
    private final Duration tokenRefreshThreshold;

    public abstract AuthType getAuthType();
    public abstract String toRequestBody() throws OAuth2AdapterException;
}
