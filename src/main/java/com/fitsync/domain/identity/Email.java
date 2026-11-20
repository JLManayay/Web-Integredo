package com.fitsync.domain.identity;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Email {

    private static final Pattern FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String raw) {
        String v = raw == null ? "" : raw.trim().toLowerCase();
        if (!FORMAT.matcher(v).matches() || v.length() > 150) {
            throw new InvalidUserException("El correo no tiene un formato válido.");
        }
        return new Email(v);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Email e && e.value.equals(value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
