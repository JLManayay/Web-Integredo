package com.fitsync.domain.identity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Pago registrado al activar el plan Pro. */
public record Payment(UUID id, UUID userId, BigDecimal amount, String currency,
                      String method, String reference, Instant paidAt) {
}
