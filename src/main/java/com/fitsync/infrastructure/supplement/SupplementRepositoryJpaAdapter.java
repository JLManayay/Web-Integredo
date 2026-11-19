package com.fitsync.infrastructure.supplement;

import com.fitsync.domain.supplement.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SupplementRepositoryJpaAdapter implements SupplementRepository {

    private final SpringDataSupplementJpaRepository jpaRepository;

    public SupplementRepositoryJpaAdapter(SpringDataSupplementJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public SupplementPlan save(SupplementPlan plan) {
        return toDomain(jpaRepository.save(toJpaEntity(plan)));
    }

    @Override
    public Optional<SupplementPlan> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<SupplementPlan> findAllByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    private SupplementPlanJpaEntity toJpaEntity(SupplementPlan plan) {
        SupplementPlanJpaEntity entity = new SupplementPlanJpaEntity(
            plan.id(), plan.userId(), plan.linkedProgramId().orElse(null));
        List<SupplementDoseJpaEntity> doses = new ArrayList<>();
        for (SupplementDose d : plan.doses()) {
            doses.add(new SupplementDoseJpaEntity(
                d.id(), entity, d.supplementName(),
                BigDecimal.valueOf(d.amount().grams()),
                d.schedule().timeOfDay(), d.schedule().isRelativeToWorkout()));
        }
        entity.setDoses(doses);
        return entity;
    }

    private SupplementPlan toDomain(SupplementPlanJpaEntity entity) {
        SupplementPlan plan = SupplementPlan.reconstruct(entity.getId(), entity.getUserId(), entity.getProgramId());
        for (SupplementDoseJpaEntity d : entity.getDoses()) {
            DosageSchedule schedule = d.isRelativeToWorkout()
                ? DosageSchedule.relativeToWorkout(d.getTimeOfDay())
                : DosageSchedule.at(d.getTimeOfDay());
            plan.reconstructDose(d.getId(), d.getSupplementName(),
                Dosage.ofGrams(d.getAmountGrams().doubleValue()), schedule);
        }
        return plan;
    }
}
