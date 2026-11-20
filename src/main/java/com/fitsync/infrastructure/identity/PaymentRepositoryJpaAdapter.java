package com.fitsync.infrastructure.identity;

import com.fitsync.domain.identity.Payment;
import com.fitsync.domain.identity.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class PaymentRepositoryJpaAdapter implements PaymentRepository {

    private final SpringDataPaymentJpaRepository jpa;

    public PaymentRepositoryJpaAdapter(SpringDataPaymentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Payment save(Payment p) {
        jpa.save(new PaymentJpaEntity(p.id(), p.userId(), p.amount(), p.currency(), p.method(), p.reference(), p.paidAt()));
        return p;
    }

    @Override
    public List<Payment> findByUserId(UUID userId) {
        return jpa.findByUserIdOrderByPaidAtDesc(userId).stream()
            .map(e -> new Payment(e.getId(), e.getUserId(), e.getAmount(), e.getCurrency(),
                e.getMethod(), e.getReference(), e.getPaidAt()))
            .toList();
    }
}
