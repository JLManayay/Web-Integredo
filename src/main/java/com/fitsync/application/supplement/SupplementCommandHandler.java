package com.fitsync.application.supplement;

import com.fitsync.domain.supplement.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SupplementCommandHandler {

    private final SupplementRepository supplementRepository;

    // Inyección de Dependencias por constructor
    public SupplementCommandHandler(SupplementRepository supplementRepository) {
        this.supplementRepository = supplementRepository;
    }

    public SupplementPlan handle(CreateSupplementPlanCommand command) {
        SupplementPlan plan = (command.linkedProgramId() == null)
            ? SupplementPlan.create(command.userId())
            : SupplementPlan.createLinkedTo(command.userId(), command.linkedProgramId());
        return supplementRepository.save(plan);
    }

    public SupplementPlan handle(ScheduleDoseCommand command) {
        SupplementPlan plan = findOrThrow(command.planId());
        DosageSchedule schedule = command.relativeToWorkout()
            ? DosageSchedule.relativeToWorkout(command.timeOfDay())
            : DosageSchedule.at(command.timeOfDay());
        plan.scheduleDose(command.supplementName(), Dosage.ofGrams(command.grams()), schedule);
        return supplementRepository.save(plan);
    }

    private SupplementPlan findOrThrow(UUID planId) {
        return supplementRepository.findById(planId)
            .orElseThrow(() -> new InvalidSupplementPlanException(
                "No existe un plan de suplementación con id " + planId));
    }
}
