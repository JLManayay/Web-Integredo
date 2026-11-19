package com.fitsync.domain.workout;

import java.util.Objects;

public final class MuscleGroup {

    private final String name;

    private MuscleGroup(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El grupo muscular no puede estar vacío.");
        }
        this.name = name.trim();
    }

    public static MuscleGroup of(String name) {
        return new MuscleGroup(name);
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MuscleGroup)) return false;
        MuscleGroup that = (MuscleGroup) o;
        return name.equalsIgnoreCase(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

    @Override
    public String toString() {
        return name;
    }
}
