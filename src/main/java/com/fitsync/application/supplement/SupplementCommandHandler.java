package com.fitsync.application.supplement;

import com.fitsync.application.identity.PlanRequiredException;
import com.fitsync.domain.identity.User;
import com.fitsync.domain.identity.UserRepository;
import com.fitsync.domain.supplement.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SupplementCommandHandler {

    static final int FREE_PLAN_LIMIT = 1;

    private final SupplementRepository supplementRepository;
    private final UserRepository userRepository;

    // Inyección de Dependencias por constructor
    public SupplementCommandHandler(SupplementRepository supplementRepository, UserRepository userRepository) {
        this.supplementRepository = supplementRepository;
        this.userRepository = userRepository;
    }

    public SupplementPlan handle(CreateSupplementPlanCommand command) {
        boolean pro = userRepository.findById(command.userId()).map(User::isPro).orElse(false);
        if (!pro && supplementRepository.findAllByUserId(command.userId()).size() >= FREE_PLAN_LIMIT) {
            throw new PlanRequiredException(
                "El plan Gratis permite un solo plan de suplementación. Pasa a Pro para crear más.");
        }
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
