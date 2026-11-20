package com.fitsync.application.identity;

import com.fitsync.domain.identity.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.HexFormat;

/** Escritura del contexto Identidad: registro, inicio y cierre de sesión. */
@Service
public class AuthCommandHandler {

    private static final Duration SESSION_TTL = Duration.ofDays(7);
    private static final int MIN_PASSWORD = 8;

    private final UserRepository users;
    private final SessionRepository sessions;
    private final PasswordHasher hasher;
    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    public AuthCommandHandler(UserRepository users, SessionRepository sessions, PasswordHasher hasher, Clock clock) {
        this.users = users;
        this.sessions = sessions;
        this.hasher = hasher;
        this.clock = clock;
    }

    public AuthResult handle(RegisterCommand command) {
        Email email = Email.of(command.email());
        if (users.findByEmail(email).isPresent()) {
            throw new InvalidUserException("Ya existe una cuenta con ese correo.");
        }
        if (command.password() == null || command.password().length() < MIN_PASSWORD) {
            throw new InvalidUserException("La contraseña debe tener al menos 8 caracteres.");
        }
        User user = users.save(User.register(command.fullName(), email, hasher.hash(command.password()), command.role()));
        return new AuthResult(openSession(user), user);
    }

    public AuthResult handle(LoginCommand command) {
        User user = users.findByEmail(Email.of(command.email()))
            .filter(u -> command.password() != null && hasher.matches(command.password(), u.passwordHash()))
            .orElseThrow(() -> new UnauthorizedException("Correo o contraseña incorrectos."));
        return new AuthResult(openSession(user), user);
    }

    public void logout(String token) {
        sessions.delete(hashToken(token));
    }

    private String openSession(User user) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sessions.save(new Session(hashToken(token), user.id(), clock.instant().plus(SESSION_TTL)));
        return token;
    }

    static String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
