package com.github.rahmnathan.oidc.adapter.keycloak.domain.credential;

import lombok.Data;

import java.time.temporal.ChronoUnit;

@Data
public class Duration {
    private final ChronoUnit unit;
    private final long value;
}
