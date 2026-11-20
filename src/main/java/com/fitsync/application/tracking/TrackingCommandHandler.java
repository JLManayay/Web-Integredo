package com.fitsync.application.tracking;

import com.fitsync.domain.tracking.*;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class TrackingCommandHandler {

    private final CheckInRepository checkIns;
    private final ScheduleAssembler assembler;
    private final Clock clock;

    public TrackingCommandHandler(CheckInRepository checkIns, ScheduleAssembler assembler, Clock clock) {
        this.checkIns = checkIns;
        this.assembler = assembler;
        this.clock = clock;
    }

    public void handle(CheckInCommand c) {
        var existing = checkIns.find(c.userId(), c.date(), c.kind(), c.refId());
        if (!c.done()) {
            existing.ifPresent(checkIns::delete);
            return;
        }
        if (existing.isPresent()) {
            return;
        }
        WeekSchedule schedule = assembler.build(c.userId(), c.programId(), c.planId());
        AdherenceTracker.ensureCanCheckIn(schedule, c.date(), c.kind(), c.refId(), LocalDate.now(clock));
        checkIns.save(new CheckIn(UUID.randomUUID(), c.userId(), c.date(), c.kind(), c.refId()));
    }
}
