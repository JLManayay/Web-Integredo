package com.fitsync.domain.supplement;

import java.util.Objects;
import java.util.UUID;

/** Entidad interna del agregado SupplementPlan. */
public final class SupplementDose {

    private final UUID id;
    private final String supplementName;
    private final Dosage amount;
    private final DosageSchedule schedule;

    SupplementDose(UUID id, String supplementName, Dosage amount, DosageSchedule schedule) {
        if (supplementName == null || supplementName.isBlank()) {
            throw new IllegalArgumentException("El suplemento debe tener un nombre.");
        }
        this.id = Objects.requireNonNull(id);
        this.supplementName = supplementName.trim();
        this.amount = Objects.requireNonNull(amount);
        this.schedule = Objects.requireNonNull(schedule);
    }

    public UUID id() { return id; }
    public String supplementName() { return supplementName; }
    public Dosage amount() { return amount; }
    public DosageSchedule schedule() { return schedule; }
}
