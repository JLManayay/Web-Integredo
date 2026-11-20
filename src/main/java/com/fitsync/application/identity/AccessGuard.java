package com.fitsync.application.identity;

import com.fitsync.domain.identity.User;
import com.fitsync.domain.identity.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/** Decide de quién son los datos que se piden: los propios o los de un atleta a cargo. */
@Service
public class AccessGuard {

    private final UserRepository users;

    public AccessGuard(UserRepository users) {
        this.users = users;
    }

    /** Devuelve el dueño de los datos: el propio usuario o un atleta suyo si es entrenador. */
    public UUID resolveOwner(CurrentUser me, UUID requestedUserId) {
        if (requestedUserId == null || requestedUserId.equals(me.id())) {
            return me.id();
        }
        User target = users.findById(requestedUserId)
            .orElseThrow(() -> new ForbiddenException("No tienes acceso a los datos de ese usuario."));
        boolean myAthlete = me.isCoach() && target.coachId().map(me.id()::equals).orElse(false);
        if (!myAthlete) {
            throw new ForbiddenException("No tienes acceso a los datos de ese usuario.");
        }
        return requestedUserId;
    }

    public void ensureCanAccess(CurrentUser me, UUID ownerId) {
        resolveOwner(me, ownerId);
    }
}
