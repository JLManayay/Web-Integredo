package com.fitsync.application.workout;

import com.fitsync.domain.workout.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

// lectura: tambien con mock, sin BD
@ExtendWith(MockitoExtension.class)
class WorkoutQueryHandlerTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Test
    void throwsWhenProgramDoesNotExist() {
        UUID missingId = UUID.randomUUID();
        when(workoutRepository.findById(missingId)).thenReturn(Optional.empty());

        WorkoutQueryHandler handler = new WorkoutQueryHandler(workoutRepository);

        org.junit.jupiter.api.Assertions.assertThrows(
            InvalidWorkoutScheduleException.class,
            () -> handler.handle(new GetWorkoutProgramQuery(missingId))
        );
    }
}
