package com.fitsync.interfaces.identity;

import com.fitsync.application.identity.*;
import com.fitsync.domain.identity.User;
import com.fitsync.interfaces.identity.dto.IdentityDtos.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthCommandHandler commands;
    private final AuthQueryHandler queries;

    public AuthController(AuthCommandHandler commands, AuthQueryHandler queries) {
        this.commands = commands;
        this.queries = queries;
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest r) {
        AuthResult result = commands.handle(new RegisterCommand(r.fullName(), r.email(), r.password(), r.role()));
        return new AuthResponse(result.token(), UserResponse.from(result.user(), null));
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest r) {
        AuthResult result = commands.handle(new LoginCommand(r.email(), r.password()));
        User coach = queries.coachOf(result.user()).orElse(null);
        return new AuthResponse(result.token(), UserResponse.from(result.user(), coach));
    }

    @PostMapping("/auth/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            commands.logout(header.substring(7).trim());
        }
    }

    @GetMapping("/me")
    public UserResponse me(CurrentUser me) {
        User user = queries.account(me.id());
        return UserResponse.from(user, queries.coachOf(user).orElse(null));
    }
}
