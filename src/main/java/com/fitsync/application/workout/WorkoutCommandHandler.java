package com.fitsync.application.workout;

import com.fitsync.domain.workout.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WorkoutCommandHandler {

    private final WorkoutRepository workoutRepository;

    public WorkoutCommandHandler(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public WorkoutProgram handle(CreateWorkoutProgramCommand command) {
        WorkoutProgram program = WorkoutProgram.create(
            command.userId(), command.name(), command.splitType()
        );
        return workoutRepository.save(program);
    }

    public WorkoutProgram handle(AddDayCommand command) {
        WorkoutProgram program = findOrThrow(command.workoutProgramId());
        program.addDay(command.dayOfWeek());
        return workoutRepository.save(program);
    }

    public WorkoutProgram handle(AddExerciseCommand command) {
        WorkoutProgram program = findOrThrow(command.workoutProgramId());
        program.addExercise(
            command.dayOfWeek(),
            command.exerciseName(),
            MuscleGroup.of(command.muscleGroup()),
            Reps.of(command.sets(), command.repsPerSet()),
            RestPeriod.ofSeconds(command.restSeconds())
        );
        return workoutRepository.save(program);
    }

    public WorkoutProgram handle(PublishWorkoutProgramCommand command) {
        WorkoutProgram program = findOrThrow(command.workoutProgramId());
        program.publish();
        return workoutRepository.save(program);
    }

    private WorkoutProgram findOrThrow(UUID programId) {
        return workoutRepository.findById(programId)
            .orElseThrow(() -> new InvalidWorkoutScheduleException(
                "No existe un programa de entrenamiento con id " + programId
            ));
    }
}
