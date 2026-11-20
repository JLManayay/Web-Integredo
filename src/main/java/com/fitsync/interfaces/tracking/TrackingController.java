package com.fitsync.interfaces.tracking;

import com.fitsync.application.identity.AccessGuard;
import com.fitsync.application.identity.CurrentUser;
import com.fitsync.application.identity.ForbiddenException;
import com.fitsync.application.tracking.*;
import com.fitsync.domain.tracking.CheckInKind;
import com.fitsync.domain.tracking.WeeklyAdherence;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TrackingController {

    public record CheckInRequest(@NotNull LocalDate date, @NotNull CheckInKind kind, @NotNull UUID refId,
                                 UUID programId, UUID planId, boolean done) {}

    public record DoseItem(UUID id, String name, double grams, String time, boolean postWorkout, String status) {}

    public record TodayResponse(LocalDate date, String now, boolean trainingDay, String programName,
                                boolean workoutDone, List<DoseItem> doses) {
        static TodayResponse from(TodayView v) {
            return new TodayResponse(v.date(), v.now().toString(), v.trainingDay(), v.programName(), v.workoutDone(),
                v.doses().stream().map(s -> new DoseItem(s.dose().id(), s.dose().name(), s.dose().grams(),
                    s.dose().time().toString(), s.dose().postWorkout(), s.status().name())).toList());
        }
    }

    public record HistoryResponse(boolean limited, List<WeeklyAdherence> weeks) {}

    private final TrackingCommandHandler commands;
    private final TrackingQueryHandler queries;
    private final AccessGuard guard;

    public TrackingController(TrackingCommandHandler commands, TrackingQueryHandler queries, AccessGuard guard) {
        this.commands = commands;
        this.queries = queries;
        this.guard = guard;
    }

    /** Lo de hoy (entrenamiento y dosis con su estado). Alimenta el panel "Hoy" y los recordatorios. */
    @GetMapping("/today")
    public TodayResponse today(CurrentUser me, @RequestParam(required = false) UUID userId,
                               @RequestParam(required = false) UUID programId, @RequestParam(required = false) UUID planId) {
        UUID owner = guard.resolveOwner(me, userId);
        return TodayResponse.from(queries.today(owner, programId, planId));
    }

    /** Solo el propio atleta marca lo que cumplió; el entrenador solo lo ve. */
    @PostMapping("/adherence/check-ins")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkIn(CurrentUser me, @Valid @RequestBody CheckInRequest r) {
        commands.handle(new CheckInCommand(me.id(), r.date(), r.kind(), r.refId(), r.programId(), r.planId(), r.done()));
    }

    @GetMapping("/adherence/history")
    public HistoryResponse history(CurrentUser me, @RequestParam(required = false) UUID userId,
                                   @RequestParam(required = false) UUID programId, @RequestParam(required = false) UUID planId,
                                   @RequestParam(defaultValue = "8") int weeks) {
        UUID owner = guard.resolveOwner(me, userId);
        if (programId == null && planId == null) {
            throw new ForbiddenException("Elige un programa o un plan para ver la adherencia.");
        }
        HistoryView view = queries.history(owner, programId, planId, weeks, me.isPro());
        return new HistoryResponse(view.limited(), view.weeks());
    }
}
