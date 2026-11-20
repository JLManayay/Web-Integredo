package com.fitsync.domain.tracking;

import java.time.LocalTime;
import java.util.UUID;

/** Copia de solo lectura de una dosis del plan, para no depender del agregado de Suplementación. */
public record PlannedDose(UUID id, String name, double grams, LocalTime time, boolean postWorkout) {
}
