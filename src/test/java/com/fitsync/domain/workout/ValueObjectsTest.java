package com.fitsync.domain.workout;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/** Pruebas de los Value Objects del contexto Entrenamiento (deuda técnica DT-01). */
class ValueObjectsTest {

    @Test
    void reps_validas_exponen_series_y_repeticiones() {
        Reps reps = Reps.of(4, 10);
        assertThat(reps.sets()).isEqualTo(4);
        assertThat(reps.repsPerSet()).isEqualTo(10);
        assertThat(reps).hasToString("4x10");
    }

    @Test
    void reps_rechaza_series_fuera_de_rango() {
        assertThatThrownBy(() -> Reps.of(0, 10)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Reps.of(11, 10)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reps_rechaza_repeticiones_fuera_de_rango() {
        assertThatThrownBy(() -> Reps.of(3, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Reps.of(3, 101)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rest_period_valido_en_segundos() {
        RestPeriod rest = RestPeriod.ofSeconds(90);
        assertThat(rest.seconds()).isEqualTo(90);
        assertThat(rest).hasToString("90s");
        assertThat(RestPeriod.ofSeconds(0).seconds()).isZero();
    }

    @Test
    void rest_period_rechaza_negativo_o_mayor_a_10_minutos() {
        assertThatThrownBy(() -> RestPeriod.ofSeconds(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> RestPeriod.ofSeconds(601)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void muscle_group_recorta_espacios_y_compara_sin_distinguir_mayusculas() {
        MuscleGroup a = MuscleGroup.of("  Pecho ");
        assertThat(a.name()).isEqualTo("Pecho");
        assertThat(a).isEqualTo(MuscleGroup.of("PECHO"));
        assertThat(a.hashCode()).isEqualTo(MuscleGroup.of("pecho").hashCode());
        assertThat(a).hasToString("Pecho");
    }

    @Test
    void muscle_group_rechaza_vacio_o_nulo() {
        assertThatThrownBy(() -> MuscleGroup.of(" ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> MuscleGroup.of(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
