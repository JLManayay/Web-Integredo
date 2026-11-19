package com.fitsync.domain.workout;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutRepository {

    WorkoutProgram save(WorkoutProgram program);

    Optional<WorkoutProgram> findById(UUID id);

    /** Todos los programas de un usuario para la pantalla de listado. */
    List<WorkoutProgram> findAllByUserId(UUID userId);
}
