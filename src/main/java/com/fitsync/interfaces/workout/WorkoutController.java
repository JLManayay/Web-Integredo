package com.fitsync.interfaces.workout;

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

    public WorkoutController(WorkoutCommandHandler commandHandler, WorkoutQueryHandler queryHandler) {
        this.commandHandler = commandHandler;
        this.queryHandler = queryHandler;
    }

    @PostMapping
    public ResponseEntity<WorkoutProgramResponse> create(@Valid @RequestBody CreateWorkoutProgramRequest request) {
        WorkoutProgram program = commandHandler.handle(
            new CreateWorkoutProgramCommand(request.userId(), request.name(), request.splitType())
        );
        WorkoutProgramResponse body = WorkoutProgramResponse.from(program);
        return ResponseEntity.created(URI.create("/api/workout-programs/" + program.id())).body(body);
    }

    @GetMapping
    public List<WorkoutProgramResponse> listByUser(@RequestParam UUID userId) {
        return queryHandler.handle(new ListWorkoutProgramsByUserQuery(userId)).stream()
            .map(WorkoutProgramResponse::from).toList();
    }

    @GetMapping("/{id}")
    public WorkoutProgramResponse getById(@PathVariable UUID id) {
        return WorkoutProgramResponse.from(queryHandler.handle(new GetWorkoutProgramQuery(id)));
    }

    @PostMapping("/{id}/days")
    public WorkoutProgramResponse addDay(@PathVariable UUID id, @Valid @RequestBody AddDayRequest request) {
        return WorkoutProgramResponse.from(commandHandler.handle(new AddDayCommand(id, request.dayOfWeek())));
    }

    @PostMapping("/{id}/exercises")
    public WorkoutProgramResponse addExercise(@PathVariable UUID id, @Valid @RequestBody AddExerciseRequest request) {
        WorkoutProgram program = commandHandler.handle(new AddExerciseCommand(
            id, request.dayOfWeek(), request.exerciseName(), request.muscleGroup(),
            request.sets(), request.repsPerSet(), request.restSeconds()
        ));
        return WorkoutProgramResponse.from(program);
    }

    @PostMapping("/{id}/publish")
    @ResponseStatus(HttpStatus.OK)
    public WorkoutProgramResponse publish(@PathVariable UUID id) {
        return WorkoutProgramResponse.from(commandHandler.handle(new PublishWorkoutProgramCommand(id)));
    }
}
