package com.github.rahmnathan.oauth2.adapter.domain.credential;

import lombok.Data;

import java.time.temporal.ChronoUnit;

@Data
public class Duration {
    private final ChronoUnit unit;
    private final long value;
}
