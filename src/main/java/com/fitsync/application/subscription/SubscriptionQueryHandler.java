package com.fitsync.application.subscription;

import com.fitsync.domain.identity.Payment;
import com.fitsync.domain.identity.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionQueryHandler {

    private final PaymentRepository payments;

    public SubscriptionQueryHandler(PaymentRepository payments) {
        this.payments = payments;
    }

    public List<Payment> paymentsOf(UUID userId) {
        return payments.findByUserId(userId);
    }
}
