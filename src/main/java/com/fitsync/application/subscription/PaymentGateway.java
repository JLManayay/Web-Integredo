package com.fitsync.application.subscription;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Puerto de la pasarela de pagos. Hoy lo implementa una pasarela simulada; para cobrar de verdad
 * se agrega otro adaptador (Culqi, Mercado Pago, Stripe) sin tocar la capa de aplicación.
 */
public interface PaymentGateway {

    /** Cobra el monto y devuelve la referencia de la operación aprobada. */
    String charge(UUID userId, BigDecimal amount, String currency);

    String method();
}
