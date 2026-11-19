package com.fitsync.domain.workout;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// pruebas del agregado WorkoutProgram, sin Spring ni base de datos
class WorkoutProgramTest {

    private final UUID userId = UUID.randomUUID();

    @Nested
    @DisplayName("Al agregar días de entrenamiento")
    class AddDay {

        @Test
        @DisplayName("agrega un día correctamente")
        void addsADaySuccessfully() {
            WorkoutProgram program = WorkoutProgram.create(userId, "Rutina PPL", SplitType.PUSH_PULL_LEGS);

            program.addDay(DayOfWeek.MONDAY);

            assertThat(program.days()).hasSize(1);
            assertThat(program.days().get(0).dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        }

        @Test
        @DisplayName("rechaza un día de la semana duplicado")
        void rejectsDuplicateDayOfWeek() {
            WorkoutProgram program = WorkoutProgram.create(userId, "Rutina PPL", SplitType.PUSH_PULL_LEGS);
            program.addDay(DayOfWeek.MONDAY);

            assertThatThrownBy(() -> program.addDay(DayOfWeek.MONDAY))
                .isInstanceOf(InvalidWorkoutScheduleException.class)
                .hasMessageContaining("MONDAY");
        }

        @Test
        @DisplayName("rechaza un séptimo día de entrenamiento (debe quedar un día de descanso)")
        void rejectsMoreThanSixTrainingDays() {
            WorkoutProgram program = WorkoutProgram.create(userId, "Rutina PPL", SplitType.PUSH_PULL_LEGS);
            DayOfWeek[] days = DayOfWeek.values();
            for (int i = 0; i < 6; i++) {
                program.addDay(days[i]);
            }

            assertThatThrownBy(() -> program.addDay(days[6]))
                .isInstanceOf(InvalidWorkoutScheduleException.class)
                .hasMessageContaining("día de descanso");
        }
    }

    @Nested
    @DisplayName("Al agregar ejercicios")
    class AddExercise {

        @Test
        @DisplayName("agrega un ejercicio a un día existente")
        void addsExerciseToExistingDay() {
            WorkoutProgram program = WorkoutProgram.create(userId, "Rutina PPL", SplitType.PUSH_PULL_LEGS);
            program.addDay(DayOfWeek.MONDAY);

            program.addExercise(
                DayOfWeek.MONDAY, "Press de banca", MuscleGroup.of("Pecho"),
                Reps.of(4, 8), RestPeriod.ofSeconds(90)
            );

            assertThat(program.days().get(0).exercises()).hasSize(1);
            assertThat(program.days().get(0).exercises().get(0).name()).isEqualTo("Press de banca");
        }

        @Test
        @DisplayName("rechaza un ejercicio duplicado en el mismo día")
        void rejectsDuplicateExerciseOnSameDay() {
            WorkoutProgram program = WorkoutProgram.create(userId, "Rutina PPL", SplitType.PUSH_PULL_LEGS);
            program.addDay(DayOfWeek.MONDAY);
            program.addExercise(DayOfWeek.MONDAY, "Press de banca", MuscleGroup.of("Pecho"), Reps.of(4, 8), RestPeriod.ofSeconds(90));

            assertThatThrownBy(() ->
                program.addExercise(DayOfWeek.MONDAY, "press de banca", MuscleGroup.of("Pecho"), Reps.of(3, 10), RestPeriod.ofSeconds(60))
            )
                .isInstanceOf(InvalidWorkoutScheduleException.class)
                .hasMessageContaining("ya está registrado");
        }

        @Test
        @DisplayName("rechaza agregar un ejercicio a un día no programado")
        void rejectsExerciseOnUnplannedDay() {
            WorkoutProgram program = WorkoutProgram.create(userId, "Rutina PPL", SplitType.PUSH_PULL_LEGS);

            assertThatThrownBy(() ->
                program.addExercise(DayOfWeek.FRIDAY, "Sentadilla", MuscleGroup.of("Pierna"), Reps.of(4, 6), RestPeriod.ofSeconds(120))
            ).isInstanceOf(InvalidWorkoutScheduleException.class);
        }
    }

    @Nested
    @DisplayName("Al publicar un programa")
    class Publish {

        @Test
        @DisplayName("no permite publicar un programa sin ejercicios")
        void rejectsPublishingEmptyProgram() {
            WorkoutProgram program = WorkoutProgram.create(userId, "Rutina vacía", SplitType.FULL_BODY);
            program.addDay(DayOfWeek.MONDAY);

            assertThatThrownBy(program::publish)
                .isInstanceOf(InvalidWorkoutScheduleException.class);
            assertThat(program.isPublished()).isFalse();
        }

        @Test
        @DisplayName("publica correctamente un programa con al menos un ejercicio")
        void publishesProgramWithContent() {
            WorkoutProgram program = WorkoutProgram.create(userId, "Rutina PPL", SplitType.PUSH_PULL_LEGS);
            program.addDay(DayOfWeek.MONDAY);
            program.addExercise(DayOfWeek.MONDAY, "Press de banca", MuscleGroup.of("Pecho"), Reps.of(4, 8), RestPeriod.ofSeconds(90));

            program.publish();

            assertThat(program.isPublished()).isTrue();
        }
    }
}
