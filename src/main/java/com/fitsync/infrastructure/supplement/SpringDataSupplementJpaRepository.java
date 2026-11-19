package com.fitsync.infrastructure.supplement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Spring Data: solo entiende entidades JPA. */
public interface SpringDataSupplementJpaRepository extends JpaRepository<SupplementPlanJpaEntity, UUID> {

    List<SupplementPlanJpaEntity> findByUserId(UUID userId);
}
