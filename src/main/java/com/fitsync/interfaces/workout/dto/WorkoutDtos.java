package com.fitsync.interfaces.workout.dto;

import com.fitsync.domain.workout.*;
import jakarta.validation.constraints.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WorkoutDtos {

    public record CreateWorkoutProgramRequest(
        UUID userId,
        @NotBlank String name,
        @NotNull SplitType splitType
    ) {}

    public record AddDayRequest(@NotNull DayOfWeek dayOfWeek) {}

    public record AddExerciseRequest(
        @NotNull DayOfWeek dayOfWeek,
        @NotBlank String exerciseName,
        @NotBlank String muscleGroup,
        @Min(1) @Max(10) int sets,
        @Min(1) @Max(100) int repsPerSet,
        @Min(0) long restSeconds
    ) {}

    public record ExerciseResponse(UUID id, String name, String muscleGroup, String reps, String rest) {
        public static ExerciseResponse from(Exercise e) {
            return new ExerciseResponse(e.id(), e.name(), e.muscleGroup().name(), e.reps().toString(), e.rest().toString());
        }
    }

    public record WorkoutDayResponse(UUID id, DayOfWeek dayOfWeek, List<ExerciseResponse> exercises) {
        public static WorkoutDayResponse from(WorkoutDay d) {
            return new WorkoutDayResponse(
                d.id(), d.dayOfWeek(),
                d.exercises().stream().map(ExerciseResponse::from).collect(Collectors.toList())
            );
        }
    }

    public record WorkoutProgramResponse(
        UUID id, UUID userId, String name, SplitType splitType,
        boolean published, List<WorkoutDayResponse> days
    ) {
        public static WorkoutProgramResponse from(WorkoutProgram p) {
            return new WorkoutProgramResponse(
                p.id(), p.userId(), p.name(), p.splitType(), p.isPublished(),
                p.days().stream().map(WorkoutDayResponse::from).collect(Collectors.toList())
            );
        }
    }
}
