package com.fitsync.application.identity;

import com.fitsync.domain.identity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// registro e inicio de sesión con mocks, sin base de datos
@ExtendWith(MockitoExtension.class)
class AuthCommandHandlerTest {

    @Mock
    private UserRepository users;
    @Mock
    private SessionRepository sessions;
    @Mock
    private PasswordHasher hasher;

    private AuthCommandHandler handler() {
        return new AuthCommandHandler(users, sessions, hasher, Clock.systemUTC());
    }

    @Test
    void registerHashesPasswordAndOpensSession() {
        when(users.findByEmail(any())).thenReturn(Optional.empty());
        when(hasher.hash("secreta123")).thenReturn("$2a$hash");
        when(users.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        AuthResult r = handler().handle(new RegisterCommand("Ana", "ana@fitsync.com", "secreta123", Role.ATLETA));

        assertThat(r.token()).isNotBlank();
        assertThat(r.user().passwordHash()).isEqualTo("$2a$hash");
        verify(sessions).save(any(Session.class));
    }

    @Test
    void rejectsDuplicatedEmail() {
        when(users.findByEmail(any())).thenReturn(Optional.of(
            User.register("Ana", Email.of("ana@fitsync.com"), "x", Role.ATLETA)));

        assertThatThrownBy(() -> handler().handle(new RegisterCommand("Ana", "ana@fitsync.com", "secreta123", Role.ATLETA)))
            .isInstanceOf(InvalidUserException.class);
    }

    @Test
    void rejectsShortPassword() {
        when(users.findByEmail(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler().handle(new RegisterCommand("Ana", "ana@fitsync.com", "123", Role.ATLETA)))
            .hasMessageContaining("8 caracteres");
    }

    @Test
    void loginFailsWithWrongPassword() {
        when(users.findByEmail(any())).thenReturn(Optional.of(
            User.register("Ana", Email.of("ana@fitsync.com"), "$2a$hash", Role.ATLETA)));
        when(hasher.matches("mala", "$2a$hash")).thenReturn(false);

        assertThatThrownBy(() -> handler().handle(new LoginCommand("ana@fitsync.com", "mala")))
            .isInstanceOf(UnauthorizedException.class);
        verify(sessions, never()).save(any());
    }
}
