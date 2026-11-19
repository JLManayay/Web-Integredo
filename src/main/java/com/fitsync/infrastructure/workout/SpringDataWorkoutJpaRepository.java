package com.fitsync.infrastructure.workout;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataWorkoutJpaRepository extends JpaRepository<WorkoutProgramJpaEntity, UUID> {

    /** Spring Data genera la consulta SQL a partir del nombre del método */
    List<WorkoutProgramJpaEntity> findByUserIdOrderByNameAsc(UUID userId);
}
