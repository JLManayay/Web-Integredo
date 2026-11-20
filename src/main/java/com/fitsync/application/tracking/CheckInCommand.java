package com.fitsync.application.tracking;

import com.fitsync.domain.tracking.CheckInKind;

import java.time.LocalDate;
import java.util.UUID;

/** Marcar (done = true) o desmarcar (done = false) un entrenamiento o una dosis. */
public record CheckInCommand(UUID userId, LocalDate date, CheckInKind kind, UUID refId,
                             UUID programId, UUID planId, boolean done) {
}
