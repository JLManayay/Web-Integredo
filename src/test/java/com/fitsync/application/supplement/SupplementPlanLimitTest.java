package com.fitsync.application.supplement;

import com.fitsync.application.identity.PlanRequiredException;
import com.fitsync.domain.identity.*;
import com.fitsync.domain.supplement.SupplementPlan;
import com.fitsync.domain.supplement.SupplementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// el plan Gratis permite un solo plan de suplementación
@ExtendWith(MockitoExtension.class)
class SupplementPlanLimitTest {

    @Mock
    private SupplementRepository plans;
    @Mock
    private UserRepository users;

    @Test
    void freeUserCannotCreateSecondPlan() {
        User u = User.register("Ana", Email.of("ana@fitsync.com"), "x", Role.ATLETA);
        when(users.findById(u.id())).thenReturn(Optional.of(u));
        when(plans.findAllByUserId(u.id())).thenReturn(List.of(SupplementPlan.create(u.id())));

        assertThatThrownBy(() -> new SupplementCommandHandler(plans, users).handle(new CreateSupplementPlanCommand(u.id(), null)))
            .isInstanceOf(PlanRequiredException.class);
        verify(plans, never()).save(any());
    }

    @Test
    void proUserCanCreateMorePlans() {
        User u = User.register("Ana", Email.of("ana@fitsync.com"), "x", Role.ATLETA);
        u.upgradeToPro(Instant.now());
        when(users.findById(u.id())).thenReturn(Optional.of(u));
        when(plans.save(any())).thenAnswer(i -> i.getArgument(0));

        new SupplementCommandHandler(plans, users).handle(new CreateSupplementPlanCommand(u.id(), null));

        verify(plans).save(any());
    }
}
