package com.fitsync.domain.supplement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Puerto del Repository Pattern para el contexto Suplementación */
public interface SupplementRepository {

    SupplementPlan save(SupplementPlan plan);

    Optional<SupplementPlan> findById(UUID id);

    List<SupplementPlan> findAllByUserId(UUID userId);
}
