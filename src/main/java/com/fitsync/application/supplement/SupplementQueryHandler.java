package com.fitsync.application.supplement;

import com.fitsync.domain.supplement.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplementQueryHandler {

    private final SupplementRepository supplementRepository;

    public SupplementQueryHandler(SupplementRepository supplementRepository) {
        this.supplementRepository = supplementRepository;
    }

    public SupplementPlan handle(GetSupplementPlanQuery query) {
        return supplementRepository.findById(query.planId())
            .orElseThrow(() -> new InvalidSupplementPlanException(
                "No existe un plan de suplementación con id " + query.planId()));
    }

    public List<SupplementPlan> handle(ListSupplementPlansByUserQuery query) {
        return supplementRepository.findAllByUserId(query.userId());
    }
}
