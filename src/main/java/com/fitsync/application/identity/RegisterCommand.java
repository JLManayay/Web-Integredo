package com.fitsync.application.identity;

import com.fitsync.domain.identity.Role;

public record RegisterCommand(String fullName, String email, String password, Role role) {
}
