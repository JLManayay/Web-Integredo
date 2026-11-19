package com.fitsync.infrastructure.workout;

import com.fitsync.domain.workout.SplitType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workout_programs")
public class WorkoutProgramJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "split_type", nullable = false)
    private SplitType splitType;

    @Column(nullable = false)
    private boolean published;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutDayJpaEntity> days = new ArrayList<>();

    protected WorkoutProgramJpaEntity() {
        // Constructor vacío exigido
    }

    public WorkoutProgramJpaEntity(UUID id, UUID userId, String name, SplitType splitType, boolean published) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.splitType = splitType;
        this.published = published;
    }

    // Getters y setters

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public SplitType getSplitType() { return splitType; }
    public boolean isPublished() { return published; }
    public List<WorkoutDayJpaEntity> getDays() { return days; }
    public void setDays(List<WorkoutDayJpaEntity> days) { this.days = days; }
}
