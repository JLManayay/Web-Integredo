package com.fitsync.application.identity;

import com.fitsync.domain.identity.SessionRepository;
import com.fitsync.domain.identity.User;
import com.fitsync.domain.identity.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

/** Lectura del contexto Identidad: resolver la sesión y consultar la cuenta. */
@Service
public class AuthQueryHandler {

    private final UserRepository users;
    private final SessionRepository sessions;
    private final Clock clock;

    public AuthQueryHandler(UserRepository users, SessionRepository sessions, Clock clock) {
        this.users = users;
        this.sessions = sessions;
        this.clock = clock;
    }

    public Optional<CurrentUser> currentUser(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return sessions.findByTokenHash(AuthCommandHandler.hashToken(token))
            .filter(s -> !s.isExpired(clock.instant()))
            .flatMap(s -> users.findById(s.userId()))
            .map(CurrentUser::from);
    }

    public User account(UUID userId) {
        return users.findById(userId).orElseThrow(() -> new UnauthorizedException("La cuenta ya no existe."));
    }

    public Optional<User> coachOf(User athlete) {
        return athlete.coachId().flatMap(users::findById);
    }
}
