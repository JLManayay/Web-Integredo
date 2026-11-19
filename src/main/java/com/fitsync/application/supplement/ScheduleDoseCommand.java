package com.fitsync.application.supplement;

import java.time.LocalTime;
import java.util.UUID;

/** usuario: programar una dosis dentro de un plan */
public record ScheduleDoseCommand(
    UUID planId,
    String supplementName,
    double grams,
    LocalTime timeOfDay,
    boolean relativeToWorkout
) {
}
