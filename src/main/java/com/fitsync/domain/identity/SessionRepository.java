package com.fitsync.domain.identity;

import java.util.Optional;

public interface SessionRepository {

    void save(Session session);

    Optional<Session> findByTokenHash(String tokenHash);

    void delete(String tokenHash);
}
