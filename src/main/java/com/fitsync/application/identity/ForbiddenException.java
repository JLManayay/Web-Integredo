package com.fitsync.application.identity;

/** Sesión válida pero sin permiso sobre esos datos (403). */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
