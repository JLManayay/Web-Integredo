package com.fitsync.infrastructure.tracking;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "adherence_checkins")
public class CheckInJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "check_date", nullable = false)
    private LocalDate checkDate;

    @Column(nullable = false)
    private String kind;

    @Column(name = "ref_id", nullable = false)
    private UUID refId;

    protected CheckInJpaEntity() {
    }

    public CheckInJpaEntity(UUID id, UUID userId, LocalDate checkDate, String kind, UUID refId) {
        this.id = id;
        this.userId = userId;
        this.checkDate = checkDate;
        this.kind = kind;
        this.refId = refId;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public LocalDate getCheckDate() { return checkDate; }
    public String getKind() { return kind; }
    public UUID getRefId() { return refId; }
}
