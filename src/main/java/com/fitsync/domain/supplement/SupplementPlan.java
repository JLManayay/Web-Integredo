package com.fitsync.domain.supplement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class SupplementPlan {

    private final UUID id;
    private final UUID userId;
    private final UUID linkedProgramId; // puede ser null: el vínculo es opcional
    private final List<SupplementDose> doses = new ArrayList<>();

    private SupplementPlan(UUID id, UUID userId, UUID linkedProgramId) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.linkedProgramId = linkedProgramId;
    }

    public static SupplementPlan create(UUID userId) {
        return new SupplementPlan(UUID.randomUUID(), userId, null);
    }

    public static SupplementPlan createLinkedTo(UUID userId, UUID workoutProgramId) {
        return new SupplementPlan(UUID.randomUUID(), userId, Objects.requireNonNull(workoutProgramId));
    }

    /** Rehidratación desde la base de datos */
    public static SupplementPlan reconstruct(UUID id, UUID userId, UUID linkedProgramId) {
        return new SupplementPlan(id, userId, linkedProgramId);
    }

    /** Rehidrata una dosis ya persistida */
    public void reconstructDose(UUID doseId, String supplementName, Dosage amount, DosageSchedule schedule) {
        doses.add(new SupplementDose(doseId, supplementName, amount, schedule));
    }

    /** evita dosis duplicadas por error de agregación */
    public void scheduleDose(String supplementName, Dosage amount, DosageSchedule schedule) {
        boolean duplicated = doses.stream().anyMatch(d ->
            d.supplementName().equalsIgnoreCase(supplementName)
                && d.schedule().timeOfDay().equals(schedule.timeOfDay()));
        if (duplicated) {
            throw new InvalidSupplementPlanException(
                "Ya existe una dosis de \"" + supplementName + "\" programada a esa hora."
            );
        }
        doses.add(new SupplementDose(UUID.randomUUID(), supplementName, amount, schedule));
    }

    public UUID id() { return id; }
    public UUID userId() { return userId; }
    public Optional<UUID> linkedProgramId() { return Optional.ofNullable(linkedProgramId); }
    public List<SupplementDose> doses() { return Collections.unmodifiableList(doses); }
}
