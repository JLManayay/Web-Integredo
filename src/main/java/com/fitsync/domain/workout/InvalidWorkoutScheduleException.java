package com.fitsync.domain.workout;

/** fase Red del ciclo TDD */
public class InvalidWorkoutScheduleException extends RuntimeException {
    public InvalidWorkoutScheduleException(String message) {
        super(message);
    }
}
