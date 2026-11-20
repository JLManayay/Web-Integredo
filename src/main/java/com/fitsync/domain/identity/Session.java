package com.fitsync.domain.identity;

import java.time.Instant;
import java.util.UUID;

/** Sesión abierta: se guarda el hash del token, nunca el token. */
public record Session(String tokenHash, UUID userId, Instant expiresAt) {

    public boolean isExpired(Instant now) {
        return !now.isBefore(expiresAt);
    }
}
