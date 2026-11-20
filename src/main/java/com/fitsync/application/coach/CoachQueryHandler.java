package com.fitsync.application.coach;

import com.fitsync.application.tracking.TrackingQueryHandler;
import com.fitsync.domain.identity.User;
import com.fitsync.domain.identity.UserRepository;
import com.fitsync.domain.supplement.SupplementPlan;
import com.fitsync.domain.supplement.SupplementRepository;
import com.fitsync.domain.workout.WorkoutProgram;
import com.fitsync.domain.workout.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/** Lectura del entrenador: sus atletas con un resumen de la semana. */
@Service
public class CoachQueryHandler {

    public record AthleteSummary(User athlete, int programs, int plans, String programName, Integer weekPercent) {
    }

    private final UserRepository users;
    private final WorkoutRepository workouts;
    private final SupplementRepository supplements;
    private final TrackingQueryHandler tracking;

    public CoachQueryHandler(UserRepository users, WorkoutRepository workouts,
                             SupplementRepository supplements, TrackingQueryHandler tracking) {
        this.users = users;
        this.workouts = workouts;
        this.supplements = supplements;
        this.tracking = tracking;
    }

    public List<AthleteSummary> athletesOf(UUID coachId) {
        return users.findAthletesOf(coachId).stream().map(this::summary).toList();
    }

    private AthleteSummary summary(User athlete) {
        List<WorkoutProgram> programs = workouts.findAllByUserId(athlete.id());
        List<SupplementPlan> plans = supplements.findAllByUserId(athlete.id());
        if (programs.isEmpty()) {
            return new AthleteSummary(athlete, 0, plans.size(), null, null);
        }
        WorkoutProgram main = programs.get(0);
        UUID planId = plans.stream()
            .filter(p -> p.linkedProgramId().map(main.id()::equals).orElse(false))
            .map(SupplementPlan::id).findFirst()
            .orElse(plans.isEmpty() ? null : plans.get(0).id());
        int percent = tracking.currentWeek(athlete.id(), main.id(), planId).percent();
        return new AthleteSummary(athlete, programs.size(), plans.size(), main.name(), percent);
    }
}
