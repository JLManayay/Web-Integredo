package com.fitsync.infrastructure.supplement;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "supplement_doses")
public class SupplementDoseJpaEntity {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private SupplementPlanJpaEntity plan;

    @Column(name = "supplement_name", nullable = false)
    private String supplementName;

    @Column(name = "amount_grams", nullable = false, precision = 8, scale = 2)
    private BigDecimal amountGrams;

    @Column(name = "time_of_day", nullable = false)
    private LocalTime timeOfDay;

    @Column(name = "relative_to_workout", nullable = false)
    private boolean relativeToWorkout;

    protected SupplementDoseJpaEntity() {}

    public SupplementDoseJpaEntity(UUID id, SupplementPlanJpaEntity plan, String supplementName,
                                   BigDecimal amountGrams, LocalTime timeOfDay, boolean relativeToWorkout) {
        this.id = id;
        this.plan = plan;
        this.supplementName = supplementName;
        this.amountGrams = amountGrams;
        this.timeOfDay = timeOfDay;
        this.relativeToWorkout = relativeToWorkout;
    }

    public UUID getId() { return id; }
    public String getSupplementName() { return supplementName; }
    public BigDecimal getAmountGrams() { return amountGrams; }
    public LocalTime getTimeOfDay() { return timeOfDay; }
    public boolean isRelativeToWorkout() { return relativeToWorkout; }
}
