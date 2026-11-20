package com.fitsync.interfaces.supplement.dto;

import com.fitsync.domain.supplement.SupplementDose;
import com.fitsync.domain.supplement.SupplementPlan;
import jakarta.validation.constraints.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class SupplementDtos {

    /** userId vacío = el propio usuario; un entrenador puede indicar a uno de sus atletas. */
    public record CreateSupplementPlanRequest(UUID userId, UUID linkedProgramId) {}

    public record ScheduleDoseRequest(
        @NotBlank String supplementName,
        @Positive double grams,
        @NotNull LocalTime timeOfDay,
        boolean relativeToWorkout
    ) {}

    public record DoseResponse(UUID id, String supplementName, double grams, String timeOfDay, boolean relativeToWorkout) {
        public static DoseResponse from(SupplementDose d) {
            return new DoseResponse(d.id(), d.supplementName(), d.amount().grams(),
                d.schedule().timeOfDay().toString(), d.schedule().isRelativeToWorkout());
        }
    }

    public record SupplementPlanResponse(UUID id, UUID userId, UUID linkedProgramId, List<DoseResponse> doses) {
        public static SupplementPlanResponse from(SupplementPlan p) {
            return new SupplementPlanResponse(p.id(), p.userId(), p.linkedProgramId().orElse(null),
                p.doses().stream().map(DoseResponse::from).toList());
        }
    }
}
