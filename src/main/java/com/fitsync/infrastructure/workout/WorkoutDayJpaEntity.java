package com.fitsync.infrastructure.workout;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workout_days")
public class WorkoutDayJpaEntity {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private WorkoutProgramJpaEntity program;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseJpaEntity> exercises = new ArrayList<>();

    protected WorkoutDayJpaEntity() {}

    public WorkoutDayJpaEntity(UUID id, WorkoutProgramJpaEntity program, DayOfWeek dayOfWeek) {
        this.id = id;
        this.program = program;
        this.dayOfWeek = dayOfWeek;
    }

    public UUID getId() { return id; }
    public WorkoutProgramJpaEntity getProgram() { return program; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public List<ExerciseJpaEntity> getExercises() { return exercises; }
    public void setExercises(List<ExerciseJpaEntity> exercises) { this.exercises = exercises; }
}
