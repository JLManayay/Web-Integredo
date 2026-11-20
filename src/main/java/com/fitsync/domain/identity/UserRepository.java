package com.fitsync.domain.identity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(Email email);

    List<User> findAthletesOf(UUID coachId);
}
