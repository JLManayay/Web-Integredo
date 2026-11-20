package com.fitsync.application.tracking;

import com.fitsync.domain.tracking.*;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TrackingQueryHandler {

    static final int MAX_WEEKS = 12;

    private final CheckInRepository checkIns;
    private final ScheduleAssembler assembler;
    private final Clock clock;

    public TrackingQueryHandler(CheckInRepository checkIns, ScheduleAssembler assembler, Clock clock) {
        this.checkIns = checkIns;
        this.assembler = assembler;
        this.clock = clock;
    }

    public TodayView today(UUID userId, UUID programId, UUID planId) {
        LocalDate today = LocalDate.now(clock);
        LocalTime now = LocalTime.now(clock);
        WeekSchedule schedule = assembler.build(userId, programId, planId);
        List<CheckIn> done = checkIns.findBetween(userId, today, today);
        boolean workoutDone = done.stream()
            .anyMatch(c -> c.kind() == CheckInKind.WORKOUT && c.refId().equals(programId));
        List<TodayView.DoseStatus> doses = schedule.dosesFor(today.getDayOfWeek()).stream()
            .map(d -> {
                boolean taken = done.stream().anyMatch(c -> c.kind() == CheckInKind.DOSE && c.refId().equals(d.id()));
                TodayView.Status status = taken ? TodayView.Status.TOMADA
                    : d.time().isAfter(now) ? TodayView.Status.PENDIENTE : TodayView.Status.ATRASADA;
                return new TodayView.DoseStatus(d, status);
            })
            .toList();
        return new TodayView(today, now.withNano(0), schedule.isTrainingDay(today.getDayOfWeek()),
            assembler.programName(programId), workoutDone, doses);
    }

    /** Semana actual y las anteriores. Con el plan Gratis solo se devuelve la semana actual. */
    public HistoryView history(UUID userId, UUID programId, UUID planId, int weeks, boolean pro) {
        int count = pro ? Math.max(1, Math.min(weeks, MAX_WEEKS)) : 1;
        LocalDate today = LocalDate.now(clock);
        LocalDate thisMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate from = thisMonday.minusWeeks(count - 1L);
        WeekSchedule schedule = assembler.build(userId, programId, planId);
        List<CheckIn> all = checkIns.findBetween(userId, from, today);
        List<WeeklyAdherence> result = new ArrayList<>();
        for (int i = count - 1; i >= 0; i--) {
            result.add(AdherenceTracker.weekly(thisMonday.minusWeeks(i), schedule, all, today));
        }
        return new HistoryView(!pro, result);
    }

    public WeeklyAdherence currentWeek(UUID userId, UUID programId, UUID planId) {
        return history(userId, programId, planId, 1, false).weeks().get(0);
    }
}
