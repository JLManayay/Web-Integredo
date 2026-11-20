package com.fitsync.domain.identity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// reglas de la cuenta: plan Pro, entrenador y marca blanca
class UserTest {

    private User athlete() {
        return User.register("Ana Atleta", Email.of("ana@fitsync.com"), "hash", Role.ATLETA);
    }

    private User coach(boolean pro) {
        User c = User.register("Carlos Coach", Email.of("carlos@fitsync.com"), "hash", Role.ENTRENADOR);
        if (pro) c.upgradeToPro(Instant.now());
        return c;
    }

    @Test
    @DisplayName("una cuenta nueva empieza en el plan Gratis")
    void newAccountIsFree() {
        assertThat(athlete().plan()).isEqualTo(SubscriptionPlan.GRATIS);
    }

    @Test
    @DisplayName("no se puede activar Pro dos veces")
    void cannotUpgradeTwice() {
        User u = athlete();
        u.upgradeToPro(Instant.now());
        assertThatThrownBy(() -> u.upgradeToPro(Instant.now())).isInstanceOf(InvalidUserException.class);
    }

    @Test
    @DisplayName("un entrenador Pro puede tomar a un atleta")
    void proCoachTakesAthlete() {
        User a = athlete();
        User c = coach(true);
        a.assignCoach(c);
        assertThat(c.isCoachOf(a)).isTrue();
    }

    @Test
    @DisplayName("un entrenador sin Pro no puede gestionar atletas")
    void freeCoachCannotTakeAthletes() {
        assertThatThrownBy(() -> athlete().assignCoach(coach(false)))
            .isInstanceOf(InvalidUserException.class).hasMessageContaining("plan Pro");
    }

    @Test
    @DisplayName("un atleta no puede tener dos entrenadores")
    void athleteHasOneCoach() {
        User a = athlete();
        a.assignCoach(coach(true));
        User other = User.register("Otro Coach", Email.of("otro@fitsync.com"), "hash", Role.ENTRENADOR);
        other.upgradeToPro(Instant.now());
        assertThatThrownBy(() -> a.assignCoach(other)).hasMessageContaining("otro entrenador");
    }

    @Test
    @DisplayName("la marca blanca es solo para entrenadores Pro y valida el color")
    void brandRules() {
        assertThatThrownBy(() -> coach(false).configureBrand(new Brand("Team Fuerza", "#FF6B4A")))
            .hasMessageContaining("plan Pro");
        assertThatThrownBy(() -> new Brand("Team Fuerza", "rojo")).isInstanceOf(InvalidUserException.class);
        User c = coach(true);
        c.configureBrand(new Brand("Team Fuerza", "#ff6b4a"));
        assertThat(c.brand()).get().extracting(Brand::color).isEqualTo("#FF6B4A");
    }

    @Test
    @DisplayName("volver a Gratis solo si se tiene Pro")
    void cancelPro() {
        User u = athlete();
        assertThatThrownBy(u::cancelPro).hasMessageContaining("No tienes");
        u.upgradeToPro(Instant.now());
        u.cancelPro();
        assertThat(u.plan()).isEqualTo(SubscriptionPlan.GRATIS);
        assertThat(u.proSince()).isEmpty();
    }

    @Test
    @DisplayName("solo el entrenador del atleta puede quitarlo de su lista")
    void removeCoach() {
        User a = athlete();
        User c = coach(true);
        a.assignCoach(c);
        assertThatThrownBy(() -> a.removeCoach(java.util.UUID.randomUUID())).hasMessageContaining("no está en tu lista");
        a.removeCoach(c.id());
        assertThat(a.coachId()).isEmpty();
    }

    @Test
    @DisplayName("un atleta no puede ser entrenador de otro ni personalizar marca")
    void athleteIsNotCoach() {
        User a = athlete();
        User b = User.register("Beto", Email.of("beto@fitsync.com"), "hash", Role.ATLETA);
        b.upgradeToPro(Instant.now());
        assertThatThrownBy(() -> a.assignCoach(b)).hasMessageContaining("no es de un entrenador");
        assertThatThrownBy(() -> b.configureBrand(new Brand("Mi marca", "#000000"))).hasMessageContaining("Solo los entrenadores");
        assertThatThrownBy(() -> new Brand("X", "#000000")).hasMessageContaining("entre 2 y 40");
    }

    @Test
    @DisplayName("la sesión vence en su fecha de expiración")
    void sessionExpiry() {
        Instant now = Instant.now();
        Session s = new Session("hash", java.util.UUID.randomUUID(), now.plusSeconds(60));
        assertThat(s.isExpired(now)).isFalse();
        assertThat(s.isExpired(now.plusSeconds(60))).isTrue();
    }

    @Test
    @DisplayName("el nombre de la cuenta es obligatorio")
    void nameRequired() {
        assertThatThrownBy(() -> User.register(" ", Email.of("x@fitsync.com"), "hash", Role.ATLETA))
            .isInstanceOf(InvalidUserException.class);
    }

    @Test
    @DisplayName("el correo se normaliza y se valida")
    void emailFormat() {
        assertThat(Email.of("  Ana@FitSync.com ").value()).isEqualTo("ana@fitsync.com");
        assertThatThrownBy(() -> Email.of("ana-sin-arroba")).isInstanceOf(InvalidUserException.class);
    }
}
