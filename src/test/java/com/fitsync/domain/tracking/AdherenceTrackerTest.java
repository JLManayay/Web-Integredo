package com.fitsync.domain.tracking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// adherencia: lo planificado contra lo que se marcó como cumplido
class AdherenceTrackerTest {

    private final UUID programId = UUID.randomUUID();
    private final PlannedDose creatina = new PlannedDose(UUID.randomUUID(), "Creatina", 5, LocalTime.of(8, 0), false);
    private final PlannedDose whey = new PlannedDose(UUID.randomUUID(), "Whey", 30, LocalTime.of(19, 0), true);
    // lunes y miércoles se entrena
    private final WeekSchedule schedule = new WeekSchedule(programId,
        Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), List.of(creatina, whey));
    private final LocalDate monday = LocalDate.of(2026, 11, 16);

    @Test
    @DisplayName("la dosis post-entreno solo cuenta los días de entrenamiento")
    void postWorkoutOnlyOnTrainingDays() {
        assertThat(schedule.expectedFor(DayOfWeek.MONDAY)).isEqualTo(3);   // entreno + creatina + whey
        assertThat(schedule.expectedFor(DayOfWeek.TUESDAY)).isEqualTo(1);  // solo creatina
    }

    @Test
    @DisplayName("calcula el porcentaje de la semana hasta hoy")
    void weeklyPercent() {
        List<CheckIn> done = List.of(
            new CheckIn(UUID.randomUUID(), null, monday, CheckInKind.WORKOUT, programId),
            new CheckIn(UUID.randomUUID(), null, monday, CheckInKind.DOSE, creatina.id()),
            new CheckIn(UUID.randomUUID(), null, monday.plusDays(1), CheckInKind.DOSE, creatina.id()));
        WeeklyAdherence w = AdherenceTracker.weekly(monday, schedule, done, monday.plusDays(1));
        assertThat(w.expected()).isEqualTo(4);
        assertThat(w.done()).isEqualTo(3);
        assertThat(w.percent()).isEqualTo(75);
        assertThat(w.days()).hasSize(2);
    }

    @Test
    @DisplayName("no se puede marcar un día futuro")
    void noFutureCheckIn() {
        assertThatThrownBy(() -> AdherenceTracker.ensureCanCheckIn(schedule, monday.plusDays(1),
            CheckInKind.DOSE, creatina.id(), monday)).isInstanceOf(InvalidCheckInException.class);
    }

    @Test
    @DisplayName("no se marca entrenamiento en un día de descanso")
    void noWorkoutOnRestDay() {
        assertThatThrownBy(() -> AdherenceTracker.ensureCanCheckIn(schedule, monday.plusDays(1),
            CheckInKind.WORKOUT, programId, monday.plusDays(1))).hasMessageContaining("descanso");
    }

    @Test
    @DisplayName("la whey no se marca en día de descanso")
    void noPostWorkoutDoseOnRestDay() {
        assertThatThrownBy(() -> AdherenceTracker.ensureCanCheckIn(schedule, monday.plusDays(1),
            CheckInKind.DOSE, whey.id(), monday.plusDays(1))).hasMessageContaining("post-entreno");
    }
}
