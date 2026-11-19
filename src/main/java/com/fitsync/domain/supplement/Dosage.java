package com.fitsync.domain.supplement;

/** cantidad de un suplemento en gr */
public final class Dosage {

    private final double grams;

    private Dosage(double grams) {
        if (grams <= 0 || grams > 100) {
            throw new IllegalArgumentException("La dosis debe estar entre 0 y 100 gramos.");
        }
        this.grams = grams;
    }

    public static Dosage ofGrams(double grams) {
        return new Dosage(grams);
    }

    public double grams() {
        return grams;
    }

    @Override
    public String toString() {
        return grams + "g";
    }
}
