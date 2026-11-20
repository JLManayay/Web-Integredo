package com.fitsync.interfaces.identity.dto;

import com.fitsync.domain.identity.Brand;
import com.fitsync.domain.identity.Role;
import com.fitsync.domain.identity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public class IdentityDtos {

    public record RegisterRequest(@NotBlank String fullName, @NotBlank String email,
                                  @NotBlank String password, @NotNull Role role) {}

    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    public record BrandResponse(String name, String color) {
        public static BrandResponse from(Brand b) {
            return b == null ? null : new BrandResponse(b.name(), b.color());
        }
    }

    /** Entrenador visible para el atleta: su nombre y su marca. */
    public record CoachResponse(UUID id, String fullName, BrandResponse brand) {
        public static CoachResponse from(User coach) {
            return coach == null ? null : new CoachResponse(coach.id(), coach.fullName(), BrandResponse.from(coach.brand().orElse(null)));
        }
    }

    public record UserResponse(UUID id, String fullName, String email, String role, String plan,
                               Instant proSince, BrandResponse brand, CoachResponse coach) {
        public static UserResponse from(User u, User coach) {
            return new UserResponse(u.id(), u.fullName(), u.email().value(), u.role().name(), u.plan().name(),
                u.proSince().orElse(null), BrandResponse.from(u.brand().orElse(null)), CoachResponse.from(coach));
        }
    }

    public record AuthResponse(String token, UserResponse user) {}
}
