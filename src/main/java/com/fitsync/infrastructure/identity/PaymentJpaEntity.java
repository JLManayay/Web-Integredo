package com.fitsync.infrastructure.identity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private String reference;

    @Column(name = "paid_at", nullable = false)
    private Instant paidAt;

    protected PaymentJpaEntity() {
    }

    public PaymentJpaEntity(UUID id, UUID userId, BigDecimal amount, String currency,
                            String method, String reference, Instant paidAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.method = method;
        this.reference = reference;
        this.paidAt = paidAt;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getMethod() { return method; }
    public String getReference() { return reference; }
    public Instant getPaidAt() { return paidAt; }
}
