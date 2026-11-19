package com.fitsync.domain.supplement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;

/** Pruebas de los Value Objects del contexto Suplementación (deuda técnica DT-01). */
class SupplementValueObjectsTest {

    @Test
    void dosage_valida_expone_gramos() {
        Dosage dosage = Dosage.ofGrams(5);
        assertThat(dosage.grams()).isEqualTo(5.0);
        assertThat(dosage).hasToString("5.0g");
        assertThat(Dosage.ofGrams(100).grams()).isEqualTo(100.0);
    }

    @Test
    void dosage_rechaza_cero_negativo_o_mayor_a_100() {
        assertThatThrownBy(() -> Dosage.ofGrams(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Dosage.ofGrams(-2)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Dosage.ofGrams(100.1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void schedule_fijo_no_es_relativo_al_entrenamiento() {
        DosageSchedule s = DosageSchedule.at(LocalTime.of(8, 0));
        assertThat(s.timeOfDay()).isEqualTo(LocalTime.of(8, 0));
        assertThat(s.isRelativeToWorkout()).isFalse();
    }

    @Test
    void schedule_post_entreno_es_relativo() {
        DosageSchedule s = DosageSchedule.relativeToWorkout(LocalTime.of(19, 30));
        assertThat(s.isRelativeToWorkout()).isTrue();
    }

    @Test
    void schedule_rechaza_hora_nula() {
        assertThatThrownBy(() -> DosageSchedule.at(null)).isInstanceOf(NullPointerException.class);
    }
}
