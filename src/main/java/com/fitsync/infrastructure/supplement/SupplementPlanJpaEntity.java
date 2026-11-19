package com.fitsync.infrastructure.supplement;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "supplement_plans")
public class SupplementPlanJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /** Shared Kernel: solo el id del programa, no una relación de objetos. */
    @Column(name = "program_id")
    private UUID programId;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplementDoseJpaEntity> doses = new ArrayList<>();

    protected SupplementPlanJpaEntity() {}

    public SupplementPlanJpaEntity(UUID id, UUID userId, UUID programId) {
        this.id = id;
        this.userId = userId;
        this.programId = programId;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getProgramId() { return programId; }
    public List<SupplementDoseJpaEntity> getDoses() { return doses; }
    public void setDoses(List<SupplementDoseJpaEntity> doses) { this.doses = doses; }
}
