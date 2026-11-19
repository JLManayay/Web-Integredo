package com.fitsync.domain.workout;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class WorkoutDay {

    private final UUID id;
    private final DayOfWeek dayOfWeek;
    private final List<Exercise> exercises = new ArrayList<>();

    WorkoutDay(UUID id, DayOfWeek dayOfWeek) {
        this.id = Objects.requireNonNull(id);
        this.dayOfWeek = Objects.requireNonNull(dayOfWeek);
    }

    void addExercise(Exercise exercise) {
        this.exercises.add(exercise);
    }

    public Exercise reconstructExercise(UUID exerciseId, String name, MuscleGroup muscleGroup,
                                         Reps reps, RestPeriod rest) {
        Exercise exercise = new Exercise(exerciseId, name, muscleGroup, reps, rest);
        this.exercises.add(exercise);
        return exercise;
    }

    public UUID id() {
        return id;
    }

    public DayOfWeek dayOfWeek() {
        return dayOfWeek;
    }

    public List<Exercise> exercises() {
        return Collections.unmodifiableList(exercises);
    }
}
