package com.fitsync.application.workout;

import java.util.UUID;

/** usuario: publicar un programa que ya tiene ejercicios. */
public record PublishWorkoutProgramCommand(UUID workoutProgramId) {
}
