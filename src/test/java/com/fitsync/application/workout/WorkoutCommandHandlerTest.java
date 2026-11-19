package com.fitsync.application.workout;

import com.fitsync.domain.workout.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// con un mock del repositorio no hace falta levantar la base de datos
@ExtendWith(MockitoExtension.class)
class WorkoutCommandHandlerTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Test
    void createsAndPersistsANewProgram() {
        // el mock devuelve el mismo programa que recibe
        when(workoutRepository.save(any(WorkoutProgram.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        WorkoutCommandHandler handler = new WorkoutCommandHandler(workoutRepository);
        UUID userId = UUID.randomUUID();

        // Act
        WorkoutProgram result = handler.handle(
            new CreateWorkoutProgramCommand(userId, "Rutina PPL", SplitType.PUSH_PULL_LEGS)
        );

        // Assert
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.name()).isEqualTo("Rutina PPL");
        verify(workoutRepository, times(1)).save(any(WorkoutProgram.class));
    }
}
