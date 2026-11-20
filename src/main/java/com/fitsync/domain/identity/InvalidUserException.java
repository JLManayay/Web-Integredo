package com.fitsync.domain.identity;

/** Regla de negocio violada en el contexto Identidad y Perfiles. */
public class InvalidUserException extends RuntimeException {
    public InvalidUserException(String message) {
        super(message);
    }
}
