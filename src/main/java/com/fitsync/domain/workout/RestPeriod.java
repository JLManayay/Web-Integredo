package com.fitsync.domain.workout;

import java.time.Duration;

public final class RestPeriod {

    private final Duration duration;

    private RestPeriod(Duration duration) {
        if (duration.isNegative() || duration.compareTo(Duration.ofMinutes(10)) > 0) {
            throw new IllegalArgumentException("El descanso debe estar entre 0 y 10 minutos.");
        }
        this.duration = duration;
    }

    public static RestPeriod ofSeconds(long seconds) {
        return new RestPeriod(Duration.ofSeconds(seconds));
    }

    public long seconds() {
        return duration.toSeconds();
    }

    @Override
    public String toString() {
        return seconds() + "s";
    }
}
