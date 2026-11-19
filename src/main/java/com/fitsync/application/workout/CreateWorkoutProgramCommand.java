package com.fitsync.application.workout;

import com.fitsync.domain.workout.SplitType;
import java.util.UUID;

public record CreateWorkoutProgramCommand(UUID userId, String name, SplitType splitType) {
}
