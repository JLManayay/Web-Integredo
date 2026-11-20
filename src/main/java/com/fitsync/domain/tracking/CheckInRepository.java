package com.fitsync.domain.tracking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CheckInRepository {

    CheckIn save(CheckIn checkIn);

    Optional<CheckIn> find(UUID userId, LocalDate date, CheckInKind kind, UUID refId);

    void delete(CheckIn checkIn);

    List<CheckIn> findBetween(UUID userId, LocalDate from, LocalDate to);
}
