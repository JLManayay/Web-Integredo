package com.fitsync.infrastructure.subscription;

import com.fitsync.application.subscription.PaymentGateway;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/** Pasarela de prueba: aprueba siempre y no mueve dinero real. */
@Component
public class SimulatedPaymentGateway implements PaymentGateway {

    @Override
    public String charge(UUID userId, BigDecimal amount, String currency) {
        return "SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public String method() {
        return "SIMULADO";
    }
}
