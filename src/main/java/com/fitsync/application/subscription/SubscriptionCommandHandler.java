package com.fitsync.application.subscription;

import com.fitsync.domain.identity.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

/** Escritura del plan: pasar a Pro (con pago) o volver a Gratis. */
@Service
public class SubscriptionCommandHandler {

    private final UserRepository users;
    private final PaymentRepository payments;
    private final PaymentGateway gateway;
    private final Clock clock;

    public SubscriptionCommandHandler(UserRepository users, PaymentRepository payments,
                                      PaymentGateway gateway, Clock clock) {
        this.users = users;
        this.payments = payments;
        this.gateway = gateway;
        this.clock = clock;
    }

    @Transactional
    public Payment upgradeToPro(UUID userId) {
        User user = findOrThrow(userId);
        user.upgradeToPro(clock.instant());
        var price = SubscriptionPlan.PRO.monthlyPrice();
        String reference = gateway.charge(userId, price, SubscriptionPlan.CURRENCY);
        Payment payment = payments.save(new Payment(UUID.randomUUID(), userId, price, SubscriptionPlan.CURRENCY,
            gateway.method(), reference, clock.instant()));
        users.save(user);
        return payment;
    }

    @Transactional
    public User cancelPro(UUID userId) {
        User user = findOrThrow(userId);
        user.cancelPro();
        return users.save(user);
    }

    private User findOrThrow(UUID userId) {
        return users.findById(userId).orElseThrow(() -> new InvalidUserException("La cuenta no existe."));
    }
}
