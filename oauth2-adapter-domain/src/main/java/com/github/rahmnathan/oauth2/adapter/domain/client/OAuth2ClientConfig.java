package com.github.rahmnathan.oauth2.adapter.domain.client;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class OAuth2ClientConfig {
    @NonNull
    private final String url;
    private final int timoutMs;
    private final long initialRetryDelay;
    private final int retryCount;
}
