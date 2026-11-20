package com.fitsync.interfaces.workout;

import com.fitsync.application.identity.AccessGuard;
import com.fitsync.application.identity.CurrentUser;
import com.fitsync.application.workout.*;
import com.fitsync.domain.workout.WorkoutProgram;
import com.fitsync.interfaces.workout.dto.WorkoutDtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workout-programs")
public class WorkoutController {

    private final WorkoutCommandHandler commandHandler;
    private final WorkoutQueryHandler queryHandler;
    private final AccessGuard guard;

    public WorkoutController(WorkoutCommandHandler commandHandler, WorkoutQueryHandler queryHandler, AccessGuard guard) {
        this.commandHandler = commandHandler;
        this.queryHandler = queryHandler;
        this.guard = guard;
    }

    @PostMapping
    public ResponseEntity<WorkoutProgramResponse> create(CurrentUser me, @Valid @RequestBody CreateWorkoutProgramRequest request) {
        UUID owner = guard.resolveOwner(me, request.userId());
        WorkoutProgram program = commandHandler.handle(
            new CreateWorkoutProgramCommand(owner, request.name(), request.splitType())
        );
        WorkoutProgramResponse body = WorkoutProgramResponse.from(program);
        return ResponseEntity.created(URI.create("/api/workout-programs/" + program.id())).body(body);
    }

    @GetMapping
    public List<WorkoutProgramResponse> listByUser(CurrentUser me, @RequestParam(required = false) UUID userId) {
        UUID owner = guard.resolveOwner(me, userId);
        return queryHandler.handle(new ListWorkoutProgramsByUserQuery(owner)).stream()
            .map(WorkoutProgramResponse::from).toList();
    }

    @GetMapping("/{id}")
    public WorkoutProgramResponse getById(CurrentUser me, @PathVariable UUID id) {
        return WorkoutProgramResponse.from(accessible(me, id));
    }

    @PostMapping("/{id}/days")
    public WorkoutProgramResponse addDay(CurrentUser me, @PathVariable UUID id, @Valid @RequestBody AddDayRequest request) {
        accessible(me, id);
        return WorkoutProgramResponse.from(commandHandler.handle(new AddDayCommand(id, request.dayOfWeek())));
    }

    @PostMapping("/{id}/exercises")
    public WorkoutProgramResponse addExercise(CurrentUser me, @PathVariable UUID id, @Valid @RequestBody AddExerciseRequest request) {
        accessible(me, id);
        WorkoutProgram program = commandHandler.handle(new AddExerciseCommand(
            id, request.dayOfWeek(), request.exerciseName(), request.muscleGroup(),
            request.sets(), request.repsPerSet(), request.restSeconds()
        ));
        return WorkoutProgramResponse.from(program);
    }

    @PostMapping("/{id}/publish")
    @ResponseStatus(HttpStatus.OK)
    public WorkoutProgramResponse publish(CurrentUser me, @PathVariable UUID id) {
        accessible(me, id);
        return WorkoutProgramResponse.from(commandHandler.handle(new PublishWorkoutProgramCommand(id)));
    }

    /** El programa debe ser del usuario o de un atleta a su cargo. */
    private WorkoutProgram accessible(CurrentUser me, UUID programId) {
        WorkoutProgram program = queryHandler.handle(new GetWorkoutProgramQuery(programId));
        guard.ensureCanAccess(me, program.userId());
        return program;
    }
}
