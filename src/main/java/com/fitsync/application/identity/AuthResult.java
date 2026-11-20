package com.fitsync.application.identity;

import com.fitsync.domain.identity.User;

public record AuthResult(String token, User user) {
}
