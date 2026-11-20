package com.fitsync.application.identity;

/** Puerto: la implementación con BCrypt está en infraestructura. */
public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String hash);
}
