package com.fitsync.infrastructure.identity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String plan;

    @Column(name = "pro_since")
    private Instant proSince;

    @Column(name = "coach_id")
    private UUID coachId;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "brand_color")
    private String brandColor;

    protected UserJpaEntity() {
    }

    public UserJpaEntity(UUID id, String fullName, String email, String passwordHash, String role, String plan,
                         Instant proSince, UUID coachId, String brandName, String brandColor) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.plan = plan;
        this.proSince = proSince;
        this.coachId = coachId;
        this.brandName = brandName;
        this.brandColor = brandColor;
    }

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public String getPlan() { return plan; }
    public Instant getProSince() { return proSince; }
    public UUID getCoachId() { return coachId; }
    public String getBrandName() { return brandName; }
    public String getBrandColor() { return brandColor; }
}
