package com.fitsync.domain.identity;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Aggregate Root del contexto Identidad: cuenta, rol, plan y vínculo con un entrenador. */
public final class User {

    private final UUID id;
    private final String fullName;
    private final Email email;
    private final String passwordHash;
    private final Role role;
    private SubscriptionPlan plan;
    private Instant proSince;
    private UUID coachId;
    private Brand brand;

    private User(UUID id, String fullName, Email email, String passwordHash, Role role,
                 SubscriptionPlan plan, Instant proSince, UUID coachId, Brand brand) {
        this.id = Objects.requireNonNull(id);
        this.fullName = requireName(fullName);
        this.email = Objects.requireNonNull(email);
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.role = Objects.requireNonNull(role);
        this.plan = Objects.requireNonNull(plan);
        this.proSince = proSince;
        this.coachId = coachId;
        this.brand = brand;
    }

    public static User register(String fullName, Email email, String passwordHash, Role role) {
        return new User(UUID.randomUUID(), fullName, email, passwordHash, role, SubscriptionPlan.GRATIS, null, null, null);
    }

    public static User reconstruct(UUID id, String fullName, Email email, String passwordHash, Role role,
                                   SubscriptionPlan plan, Instant proSince, UUID coachId, Brand brand) {
        return new User(id, fullName, email, passwordHash, role, plan, proSince, coachId, brand);
    }

    public void upgradeToPro(Instant now) {
        if (isPro()) {
            throw new InvalidUserException("Ya tienes el plan Pro activo.");
        }
        plan = SubscriptionPlan.PRO;
        proSince = now;
    }

    public void cancelPro() {
        if (!isPro()) {
            throw new InvalidUserException("No tienes el plan Pro activo.");
        }
        plan = SubscriptionPlan.GRATIS;
        proSince = null;
    }

    /** El atleta queda a cargo de un entrenador con plan Pro. */
    public void assignCoach(User coach) {
        if (role != Role.ATLETA) {
            throw new InvalidUserException("Solo una cuenta de atleta puede tener entrenador.");
        }
        if (coach.role != Role.ENTRENADOR) {
            throw new InvalidUserException("La cuenta indicada no es de un entrenador.");
        }
        if (!coach.isPro()) {
            throw new InvalidUserException("El entrenador necesita el plan Pro para gestionar atletas.");
        }
        if (coach.id.equals(coachId)) {
            throw new InvalidUserException("Este atleta ya está en tu lista.");
        }
        if (coachId != null) {
            throw new InvalidUserException("Este atleta ya tiene otro entrenador.");
        }
        coachId = coach.id;
    }

    public void removeCoach(UUID requestingCoachId) {
        if (coachId == null || !coachId.equals(requestingCoachId)) {
            throw new InvalidUserException("Este atleta no está en tu lista.");
        }
        coachId = null;
    }

    public void configureBrand(Brand newBrand) {
        if (role != Role.ENTRENADOR) {
            throw new InvalidUserException("Solo los entrenadores pueden personalizar la marca.");
        }
        if (!isPro()) {
            throw new InvalidUserException("La marca blanca es parte del plan Pro.");
        }
        brand = Objects.requireNonNull(newBrand);
    }

    public boolean isPro() {
        return plan == SubscriptionPlan.PRO;
    }

    public boolean isCoachOf(User athlete) {
        return role == Role.ENTRENADOR && id.equals(athlete.coachId);
    }

    private static String requireName(String name) {
        if (name == null || name.trim().length() < 2 || name.trim().length() > 120) {
            throw new InvalidUserException("El nombre debe tener entre 2 y 120 caracteres.");
        }
        return name.trim();
    }

    public UUID id() { return id; }
    public String fullName() { return fullName; }
    public Email email() { return email; }
    public String passwordHash() { return passwordHash; }
    public Role role() { return role; }
    public SubscriptionPlan plan() { return plan; }
    public Optional<Instant> proSince() { return Optional.ofNullable(proSince); }
    public Optional<UUID> coachId() { return Optional.ofNullable(coachId); }
    public Optional<Brand> brand() { return Optional.ofNullable(brand); }
}
