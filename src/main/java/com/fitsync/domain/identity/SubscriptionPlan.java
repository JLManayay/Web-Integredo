package com.fitsync.domain.identity;

import java.math.BigDecimal;

/** Modelo freemium: GRATIS sin costo y PRO con pago mensual. */
public enum SubscriptionPlan {
    GRATIS(BigDecimal.ZERO),
    PRO(new BigDecimal("29.00"));

    public static final String CURRENCY = "PEN";

    private final BigDecimal monthlyPrice;

    SubscriptionPlan(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public BigDecimal monthlyPrice() {
        return monthlyPrice;
    }
}
