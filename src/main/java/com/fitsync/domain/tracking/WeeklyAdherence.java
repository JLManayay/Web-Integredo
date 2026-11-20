package com.fitsync.domain.tracking;

import java.time.LocalDate;
import java.util.List;

public record WeeklyAdherence(LocalDate weekStart, int expected, int done, int percent, List<DayAdherence> days) {

    public record DayAdherence(LocalDate date, int expected, int done) {
    }
}
