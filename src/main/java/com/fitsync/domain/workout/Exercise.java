package com.fitsync.domain.workout;

import java.util.Objects;
import java.util.UUID;

public final class Exercise {

    private final UUID id;
    private final String name;
    private final MuscleGroup muscleGroup;
    private final Reps reps;
    private final RestPeriod rest;

    Exercise(UUID id, String name, MuscleGroup muscleGroup, Reps reps, RestPeriod rest) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El ejercicio debe tener un nombre.");
        }
        this.id = Objects.requireNonNull(id);
        this.name = name.trim();
        this.muscleGroup = Objects.requireNonNull(muscleGroup);
        this.reps = Objects.requireNonNull(reps);
        this.rest = Objects.requireNonNull(rest);
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public MuscleGroup muscleGroup() {
        return muscleGroup;
    }

    public Reps reps() {
        return reps;
    }

    public RestPeriod rest() {
        return rest;
    }

    @Override
    public String toString() {
        return name + " (" + muscleGroup + ", " + reps + ", descanso " + rest + ")";
    }
}
