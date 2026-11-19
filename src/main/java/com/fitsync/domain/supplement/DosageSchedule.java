package com.fitsync.domain.supplement;

import java.time.LocalTime;
import java.util.Objects;

/** cuándo debe tomarse una dosis */
public final class DosageSchedule {

    private final LocalTime timeOfDay;
    private final boolean relativeToWorkout;

    private DosageSchedule(LocalTime timeOfDay, boolean relativeToWorkout) {
        this.timeOfDay = Objects.requireNonNull(timeOfDay);
        this.relativeToWorkout = relativeToWorkout;
    }

    public static DosageSchedule at(LocalTime timeOfDay) {
        return new DosageSchedule(timeOfDay, false);
    }

    public static DosageSchedule relativeToWorkout(LocalTime timeOfDay) {
        return new DosageSchedule(timeOfDay, true);
    }

    public LocalTime timeOfDay() {
        return timeOfDay;
    }

    public boolean isRelativeToWorkout() {
        return relativeToWorkout;
    }
}
