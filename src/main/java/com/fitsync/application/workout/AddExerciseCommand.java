package com.fitsync.application.workout;

import java.time.DayOfWeek;
import java.util.UUID;

public record AddExerciseCommand(
    UUID workoutProgramId,
    DayOfWeek dayOfWeek,
    String exerciseName,
    String muscleGroup,
    int sets,
    int repsPerSet,
    long restSeconds
) {
}
