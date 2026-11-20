package com.fitsync.application.identity;

/** La función pedida es del plan Pro (402). */
public class PlanRequiredException extends RuntimeException {
    public PlanRequiredException(String message) {
        super(message);
    }
}
