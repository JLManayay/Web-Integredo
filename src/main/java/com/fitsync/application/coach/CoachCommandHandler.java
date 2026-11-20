package com.fitsync.application.coach;

import com.fitsync.domain.identity.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

/** Escritura del entrenador: sumar o quitar atletas y configurar la marca blanca. */
@Service
public class CoachCommandHandler {

    private final UserRepository users;

    public CoachCommandHandler(UserRepository users) {
        this.users = users;
    }

    public User linkAthlete(UUID coachId, String athleteEmail) {
        User coach = find(coachId);
        User athlete = users.findByEmail(Email.of(athleteEmail))
            .orElseThrow(() -> new InvalidUserException("No hay ninguna cuenta con ese correo."));
        athlete.assignCoach(coach);
        return users.save(athlete);
    }

    public void unlinkAthlete(UUID coachId, UUID athleteId) {
        User athlete = find(athleteId);
        athlete.removeCoach(coachId);
        users.save(athlete);
    }

    public User configureBrand(UUID coachId, String name, String color) {
        User coach = find(coachId);
        coach.configureBrand(new Brand(name, color));
        return users.save(coach);
    }

    private User find(UUID id) {
        return users.findById(id).orElseThrow(() -> new InvalidUserException("La cuenta no existe."));
    }
}
