package com.fitsync.application.workout;

import java.util.UUID;

/** Consulta: todos los programas de entrenamiento de un usuario. */
public record ListWorkoutProgramsByUserQuery(UUID userId) {
}
