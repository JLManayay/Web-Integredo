package com.fitsync.domain.workout;

public final class Reps {

    private final int sets;
    private final int repsPerSet;

    private Reps(int sets, int repsPerSet) {
        if (sets <= 0 || sets > 10) {
            throw new IllegalArgumentException("Las series deben estar entre 1 y 10.");
        }
        if (repsPerSet <= 0 || repsPerSet > 100) {
            throw new IllegalArgumentException("Las repeticiones deben estar entre 1 y 100.");
        }
        this.sets = sets;
        this.repsPerSet = repsPerSet;
    }

    public static Reps of(int sets, int repsPerSet) {
        return new Reps(sets, repsPerSet);
    }

    public int sets() {
        return sets;
    }

    public int repsPerSet() {
        return repsPerSet;
    }

    @Override
    public String toString() {
        return sets + "x" + repsPerSet;
    }
}
