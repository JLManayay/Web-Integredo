package com.fitsync.infrastructure.identity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataPaymentJpaRepository extends JpaRepository<PaymentJpaEntity, UUID> {

    List<PaymentJpaEntity> findByUserIdOrderByPaidAtDesc(UUID userId);
}
