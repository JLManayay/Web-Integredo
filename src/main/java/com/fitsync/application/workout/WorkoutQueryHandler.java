package com.fitsync.application.workout;

import com.fitsync.domain.workout.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutQueryHandler {

    private final WorkoutRepository workoutRepository;

    public WorkoutQueryHandler(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public WorkoutProgram handle(GetWorkoutProgramQuery query) {
        return workoutRepository.findById(query.workoutProgramId())
            .orElseThrow(() -> new InvalidWorkoutScheduleException(
                "No existe un programa de entrenamiento con id " + query.workoutProgramId()
            ));
    }

    public List<WorkoutProgram> handle(ListWorkoutProgramsByUserQuery query) {
        return workoutRepository.findAllByUserId(query.userId());
    }
}
