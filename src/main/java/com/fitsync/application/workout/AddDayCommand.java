package com.fitsync.application.workout;

import java.time.DayOfWeek;
import java.util.UUID;

/** usuario: agregar un día de entrenamiento al programa */
public record AddDayCommand(UUID workoutProgramId, DayOfWeek dayOfWeek) {
}
