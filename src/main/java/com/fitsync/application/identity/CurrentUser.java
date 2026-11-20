package com.fitsync.application.identity;

import com.fitsync.domain.identity.Role;
import com.fitsync.domain.identity.SubscriptionPlan;
import com.fitsync.domain.identity.User;

import java.util.UUID;

/** Quién hace la petición, resuelto a partir del token. */
public record CurrentUser(UUID id, String fullName, String email, Role role, SubscriptionPlan plan) {

    public static CurrentUser from(User user) {
        return new CurrentUser(user.id(), user.fullName(), user.email().value(), user.role(), user.plan());
    }

    public boolean isCoach() {
        return role == Role.ENTRENADOR;
    }

    public boolean isPro() {
        return plan == SubscriptionPlan.PRO;
    }
}
