package com.github.rahmnathan.oidc.adapter.keycloak.domain.credential;

import lombok.Builder;
import lombok.Data;

import java.time.temporal.ChronoUnit;

@Data
@Builder
public class Duration {
    private final ChronoUnit unit;
    private final long value;
}
