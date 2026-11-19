package com.fitsync.domain.workout;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class WorkoutProgram {

    /** Máximo de días de entrenamiento por semana: debe quedar al menos un día de descanso. */
    private static final int MAX_TRAINING_DAYS_PER_WEEK = 6;

    private final UUID id;
    private final UUID userId;
    private String name;
    private final SplitType splitType;
    private final List<WorkoutDay> days = new ArrayList<>();
    private boolean published = false;

    private WorkoutProgram(UUID id, UUID userId, String name, SplitType splitType) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.name = requireName(name);
        this.splitType = Objects.requireNonNull(splitType);
    }

    /** Factory method: forma explícita y expresiva de crear el agregado */
    public static WorkoutProgram create(UUID userId, String name, SplitType splitType) {
        return new WorkoutProgram(UUID.randomUUID(), userId, name, splitType);
    }

    public static WorkoutProgram reconstruct(UUID id, UUID userId, String name,
                                              SplitType splitType, boolean published) {
        WorkoutProgram program = new WorkoutProgram(id, userId, name, splitType);
        program.published = published;
        return program;
    }

    /** Agrega un nuevo día de entrenamiento al programa */
    public WorkoutDay addDay(DayOfWeek dayOfWeek) {
        boolean alreadyExists = days.stream().anyMatch(d -> d.dayOfWeek() == dayOfWeek);
        if (alreadyExists) {
            throw new InvalidWorkoutScheduleException(
                "Ya existe un día de entrenamiento programado para " + dayOfWeek + "."
            );
        }
        if (days.size() >= MAX_TRAINING_DAYS_PER_WEEK) {
            throw new InvalidWorkoutScheduleException(
                "Un programa no puede tener más de 6 días de entrenamiento; debe quedar al menos un día de descanso."
            );
        }
        WorkoutDay day = new WorkoutDay(UUID.randomUUID(), dayOfWeek);
        days.add(day);
        return day;
    }

    /** Agrega un ejercicio a un día ya existente del programa  */
    public void addExercise(DayOfWeek dayOfWeek, String exerciseName,
                             MuscleGroup muscleGroup, Reps reps, RestPeriod rest) {
        WorkoutDay day = findDayOrThrow(dayOfWeek);
        validateOverlap(day, exerciseName);
        Exercise exercise = new Exercise(UUID.randomUUID(), exerciseName, muscleGroup, reps, rest);
        day.addExercise(exercise);
    }

    public WorkoutDay reconstructDay(UUID dayId, DayOfWeek dayOfWeek) {
        WorkoutDay day = new WorkoutDay(dayId, dayOfWeek);
        days.add(day);
        return day;
    }

    private void validateOverlap(WorkoutDay day, String exerciseName) {
        boolean duplicated = day.exercises().stream()
            .anyMatch(e -> e.name().equalsIgnoreCase(exerciseName));
        if (duplicated) {
            throw new InvalidWorkoutScheduleException(
                "El ejercicio \"" + exerciseName + "\" ya está registrado el " + day.dayOfWeek() + "."
            );
        }
    }

    private WorkoutDay findDayOrThrow(DayOfWeek dayOfWeek) {
        return days.stream()
            .filter(d -> d.dayOfWeek() == dayOfWeek)
            .findFirst()
            .orElseThrow(() -> new InvalidWorkoutScheduleException(
                "No existe un día programado para " + dayOfWeek + "; agréguelo primero con addDay()."
            ));
    }

    public void publish() {
        boolean hasContent = days.stream().anyMatch(d -> !d.exercises().isEmpty());
        if (!hasContent) {
            throw new InvalidWorkoutScheduleException(
                "No se puede publicar un programa sin ejercicios registrados."
            );
        }
        this.published = true;
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El programa debe tener un nombre.");
        }
        return name.trim();
    }

    // Getters

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public String name() {
        return name;
    }

    public SplitType splitType() {
        return splitType;
    }

    public boolean isPublished() {
        return published;
    }

    public List<WorkoutDay> days() {
        return Collections.unmodifiableList(days);
    }
}
