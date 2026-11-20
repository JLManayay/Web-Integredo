package com.fitsync.application.tracking;

import com.fitsync.application.identity.ForbiddenException;
import com.fitsync.domain.supplement.SupplementPlan;
import com.fitsync.domain.supplement.SupplementRepository;
import com.fitsync.domain.tracking.PlannedDose;
import com.fitsync.domain.tracking.WeekSchedule;
import com.fitsync.domain.workout.WorkoutDay;
import com.fitsync.domain.workout.WorkoutProgram;
import com.fitsync.domain.workout.WorkoutRepository;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Capa anticorrupción: traduce el programa (Entrenamiento) y el plan (Suplementación)
 * al calendario que entiende el contexto Seguimiento, verificando que sean del usuario.
 */
@Component
public class ScheduleAssembler {

    private final WorkoutRepository workouts;
    private final SupplementRepository supplements;

    public ScheduleAssembler(WorkoutRepository workouts, SupplementRepository supplements) {
        this.workouts = workouts;
        this.supplements = supplements;
    }

    public WeekSchedule build(UUID userId, UUID programId, UUID planId) {
        Set<DayOfWeek> trainingDays = Set.of();
        String programName = null;
        if (programId != null) {
            WorkoutProgram program = workouts.findById(programId)
                .filter(p -> p.userId().equals(userId))
                .orElseThrow(() -> new ForbiddenException("El programa no existe o no es de este usuario."));
            trainingDays = program.days().stream().map(WorkoutDay::dayOfWeek).collect(Collectors.toSet());
        }
        List<PlannedDose> doses = List.of();
        if (planId != null) {
            SupplementPlan plan = supplements.findById(planId)
                .filter(p -> p.userId().equals(userId))
                .orElseThrow(() -> new ForbiddenException("El plan no existe o no es de este usuario."));
            doses = plan.doses().stream()
                .map(d -> new PlannedDose(d.id(), d.supplementName(), d.amount().grams(),
                    d.schedule().timeOfDay(), d.schedule().isRelativeToWorkout()))
                .toList();
        }
        return new WeekSchedule(programId, trainingDays, doses);
    }

    public String programName(UUID programId) {
        return programId == null ? null : workouts.findById(programId).map(WorkoutProgram::name).orElse(null);
    }
}
