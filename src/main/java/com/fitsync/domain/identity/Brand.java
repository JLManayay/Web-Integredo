package com.fitsync.domain.identity;

import java.util.regex.Pattern;

/** Marca blanca del entrenador: nombre y color que ven sus atletas. */
public record Brand(String name, String color) {

    private static final Pattern HEX = Pattern.compile("^#[0-9A-Fa-f]{6}$");

    public Brand {
        name = name == null ? "" : name.trim();
        if (name.length() < 2 || name.length() > 40) {
            throw new InvalidUserException("El nombre de la marca debe tener entre 2 y 40 caracteres.");
        }
        if (color == null || !HEX.matcher(color).matches()) {
            throw new InvalidUserException("El color de la marca debe tener el formato #RRGGBB.");
        }
        color = color.toUpperCase();
    }
}
