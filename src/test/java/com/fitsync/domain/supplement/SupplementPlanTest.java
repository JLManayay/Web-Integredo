package com.fitsync.domain.supplement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// pruebas del agregado SupplementPlan (sin Spring ni BD)
class SupplementPlanTest {

    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("Permite programar la misma dosis a horas distintas")
    void allowsSameSupplementAtDifferentTimes() {
        SupplementPlan plan = SupplementPlan.create(userId);
        plan.scheduleDose("Cafeína", Dosage.ofGrams(0.2), DosageSchedule.at(LocalTime.of(8, 0)));
        plan.scheduleDose("Cafeína", Dosage.ofGrams(0.2), DosageSchedule.at(LocalTime.of(14, 0)));

        assertThat(plan.doses()).hasSize(2);
    }

    @Test
    @DisplayName("Rechaza el mismo suplemento a la misma hora")
    void rejectsDuplicatedDose() {
        SupplementPlan plan = SupplementPlan.create(userId);
        plan.scheduleDose("Creatina", Dosage.ofGrams(5), DosageSchedule.at(LocalTime.of(8, 0)));

        assertThatThrownBy(() ->
            plan.scheduleDose("creatina", Dosage.ofGrams(5), DosageSchedule.at(LocalTime.of(8, 0))))
            .isInstanceOf(InvalidSupplementPlanException.class);
    }

    @Test
    @DisplayName("Rechaza dosis fuera de rango")
    void rejectsInvalidDosage() {
        assertThatThrownBy(() -> Dosage.ofGrams(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Dosage.ofGrams(101)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("El vínculo con el programa es solo un id")
    void linksToProgramById() {
        UUID programId = UUID.randomUUID();
        SupplementPlan plan = SupplementPlan.createLinkedTo(userId, programId);

        assertThat(plan.linkedProgramId()).contains(programId);
        assertThat(SupplementPlan.create(userId).linkedProgramId()).isEmpty();
    }
}
