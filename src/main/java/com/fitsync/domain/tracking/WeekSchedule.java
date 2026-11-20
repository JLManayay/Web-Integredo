package com.fitsync.domain.tracking;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Lo que el atleta debería cumplir cada día de la semana: entrenar los días del programa
 * y tomar las dosis (las post-entreno solo cuentan en días de entrenamiento).
 */
public record WeekSchedule(UUID programId, Set<DayOfWeek> trainingDays, List<PlannedDose> doses) {

    public WeekSchedule {
        trainingDays = Set.copyOf(trainingDays);
        doses = List.copyOf(doses);
    }

    public boolean isTrainingDay(DayOfWeek day) {
        return programId != null && trainingDays.contains(day);
    }

    public List<PlannedDose> dosesFor(DayOfWeek day) {
        return doses.stream()
            .filter(d -> !d.postWorkout() || isTrainingDay(day))
            .sorted(Comparator.comparing(PlannedDose::time))
            .toList();
    }

    public int expectedFor(DayOfWeek day) {
        return (isTrainingDay(day) ? 1 : 0) + dosesFor(day).size();
    }
}
