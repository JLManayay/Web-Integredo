package com.fitsync.infrastructure.tracking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataCheckInJpaRepository extends JpaRepository<CheckInJpaEntity, UUID> {

    Optional<CheckInJpaEntity> findByUserIdAndCheckDateAndKindAndRefId(UUID userId, LocalDate date, String kind, UUID refId);

    List<CheckInJpaEntity> findByUserIdAndCheckDateBetween(UUID userId, LocalDate from, LocalDate to);
}
