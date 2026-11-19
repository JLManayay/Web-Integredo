package com.fitsync.infrastructure.workout;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "exercises")
public class ExerciseJpaEntity {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "day_id", nullable = false)
    private WorkoutDayJpaEntity day;

    @Column(nullable = false)
    private String name;

    @Column(name = "muscle_group", nullable = false)
    private String muscleGroup;

    @Column(nullable = false)
    private int sets;

    @Column(name = "reps_per_set", nullable = false)
    private int repsPerSet;

    @Column(name = "rest_seconds", nullable = false)
    private long restSeconds;

    protected ExerciseJpaEntity() {}

    public ExerciseJpaEntity(UUID id, WorkoutDayJpaEntity day, String name, String muscleGroup,
                              int sets, int repsPerSet, long restSeconds) {
        this.id = id;
        this.day = day;
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.sets = sets;
        this.repsPerSet = repsPerSet;
        this.restSeconds = restSeconds;
    }

    public UUID getId() { return id; }
    public WorkoutDayJpaEntity getDay() { return day; }
    public String getName() { return name; }
    public String getMuscleGroup() { return muscleGroup; }
    public int getSets() { return sets; }
    public int getRepsPerSet() { return repsPerSet; }
    public long getRestSeconds() { return restSeconds; }
}
