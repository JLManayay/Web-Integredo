package com.fitsync.domain.tracking;

import java.time.LocalDate;
import java.util.UUID;

/** Marca de "cumplido": el entrenamiento de un día (ref = programa) o una dosis (ref = dosis). */
public record CheckIn(UUID id, UUID userId, LocalDate date, CheckInKind kind, UUID refId) {
}
