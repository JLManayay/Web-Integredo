package com.fitsync.application.identity;

/** Sin sesión válida (401). */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
