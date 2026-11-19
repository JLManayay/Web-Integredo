package com.fitsync.domain.supplement;

/** Excepción de dominio del Bounded Context Suplementación */
public class InvalidSupplementPlanException extends RuntimeException {
    public InvalidSupplementPlanException(String message) {
        super(message);
    }
}
