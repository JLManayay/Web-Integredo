package com.fitsync.domain.tracking;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Servicio de dominio: reglas del registro diario y cálculo de la adherencia semanal. */
public final class AdherenceTracker {

    private AdherenceTracker() {
    }

    public static void ensureCanCheckIn(WeekSchedule schedule, LocalDate date, CheckInKind kind,
                                        UUID refId, LocalDate today) {
        if (date.isAfter(today)) {
            throw new InvalidCheckInException("No puedes registrar un día que todavía no llega.");
        }
        DayOfWeek day = date.getDayOfWeek();
        if (kind == CheckInKind.WORKOUT) {
            if (!schedule.isTrainingDay(day)) {
                throw new InvalidCheckInException("Ese día es de descanso en tu rutina.");
            }
            if (!refId.equals(schedule.programId())) {
                throw new InvalidCheckInException("El entrenamiento no corresponde al programa seleccionado.");
            }
        } else {
            boolean planned = schedule.dosesFor(day).stream().anyMatch(d -> d.id().equals(refId));
            if (!planned) {
                throw new InvalidCheckInException(
                    "Esa dosis no corresponde a ese día (las post-entreno solo van en días de entrenamiento).");
            }
        }
    }

    /** Adherencia de la semana que empieza en weekStart, contando solo hasta "today". */
    public static WeeklyAdherence weekly(LocalDate weekStart, WeekSchedule schedule,
                                         List<CheckIn> checkIns, LocalDate today) {
        List<WeeklyAdherence.DayAdherence> days = new ArrayList<>();
        int expected = 0;
        int done = 0;
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            if (date.isAfter(today)) {
                break;
            }
            int dayExpected = schedule.expectedFor(date.getDayOfWeek());
            int dayDone = doneOn(date, schedule, checkIns);
            days.add(new WeeklyAdherence.DayAdherence(date, dayExpected, dayDone));
            expected += dayExpected;
            done += dayDone;
        }
        int percent = expected == 0 ? 0 : Math.round(done * 100f / expected);
        return new WeeklyAdherence(weekStart, expected, done, percent, days);
    }

    private static int doneOn(LocalDate date, WeekSchedule schedule, List<CheckIn> checkIns) {
        DayOfWeek day = date.getDayOfWeek();
        int count = 0;
        for (CheckIn c : checkIns) {
            if (!c.date().equals(date)) {
                continue;
            }
            if (c.kind() == CheckInKind.WORKOUT && schedule.isTrainingDay(day) && c.refId().equals(schedule.programId())) {
                count++;
            } else if (c.kind() == CheckInKind.DOSE
                && schedule.dosesFor(day).stream().anyMatch(d -> d.id().equals(c.refId()))) {
                count++;
            }
        }
        return count;
    }
}
