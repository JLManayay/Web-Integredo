package com.fitsync.infrastructure.tracking;

import com.fitsync.domain.tracking.CheckIn;
import com.fitsync.domain.tracking.CheckInKind;
import com.fitsync.domain.tracking.CheckInRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CheckInRepositoryJpaAdapter implements CheckInRepository {

    private final SpringDataCheckInJpaRepository jpa;

    public CheckInRepositoryJpaAdapter(SpringDataCheckInJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public CheckIn save(CheckIn c) {
        jpa.save(new CheckInJpaEntity(c.id(), c.userId(), c.date(), c.kind().name(), c.refId()));
        return c;
    }

    @Override
    public Optional<CheckIn> find(UUID userId, LocalDate date, CheckInKind kind, UUID refId) {
        return jpa.findByUserIdAndCheckDateAndKindAndRefId(userId, date, kind.name(), refId).map(this::toDomain);
    }

    @Override
    public void delete(CheckIn checkIn) {
        jpa.deleteById(checkIn.id());
    }

    @Override
    public List<CheckIn> findBetween(UUID userId, LocalDate from, LocalDate to) {
        return jpa.findByUserIdAndCheckDateBetween(userId, from, to).stream().map(this::toDomain).toList();
    }

    private CheckIn toDomain(CheckInJpaEntity e) {
        return new CheckIn(e.getId(), e.getUserId(), e.getCheckDate(), CheckInKind.valueOf(e.getKind()), e.getRefId());
    }
}
