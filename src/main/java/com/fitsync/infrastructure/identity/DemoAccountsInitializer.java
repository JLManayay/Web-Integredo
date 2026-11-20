package com.fitsync.infrastructure.identity;

import com.fitsync.application.identity.PasswordHasher;
import com.fitsync.domain.identity.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Deja listas dos cuentas de prueba para la demo:
 * demo@fitsync.com (atleta, la del usuario demo de V2) y entrenador@fitsync.com (entrenador).
 * Ambas con la contraseña demo1234.
 */
@Component
public class DemoAccountsInitializer implements ApplicationRunner {

    static final UUID DEMO_ATHLETE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String DEMO_PASSWORD = "demo1234";

    private final UserRepository users;
    private final PasswordHasher hasher;

    public DemoAccountsInitializer(UserRepository users, PasswordHasher hasher) {
        this.users = users;
        this.hasher = hasher;
    }

    @Override
    public void run(ApplicationArguments args) {
        users.findById(DEMO_ATHLETE_ID)
            .filter(u -> !u.passwordHash().startsWith("$2"))
            .ifPresent(u -> users.save(User.reconstruct(u.id(), "Atleta Demo", u.email(), hasher.hash(DEMO_PASSWORD),
                u.role(), u.plan(), u.proSince().orElse(null), u.coachId().orElse(null), u.brand().orElse(null))));

        Email coachEmail = Email.of("entrenador@fitsync.com");
        if (users.findByEmail(coachEmail).isEmpty()) {
            users.save(User.register("Entrenador Demo", coachEmail, hasher.hash(DEMO_PASSWORD), Role.ENTRENADOR));
        }
    }
}
