package com.fitsync.application.tracking;

import com.fitsync.domain.tracking.PlannedDose;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** Lo que toca hoy: el entrenamiento y las dosis, con su estado para los recordatorios. */
public record TodayView(LocalDate date, LocalTime now, boolean trainingDay, String programName,
                        boolean workoutDone, List<DoseStatus> doses) {

    public enum Status { TOMADA, PENDIENTE, ATRASADA }

    public record DoseStatus(PlannedDose dose, Status status) {
    }
}
