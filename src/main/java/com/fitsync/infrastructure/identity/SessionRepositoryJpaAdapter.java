package com.fitsync.infrastructure.identity;

import com.fitsync.domain.identity.Session;
import com.fitsync.domain.identity.SessionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SessionRepositoryJpaAdapter implements SessionRepository {

    private final SpringDataSessionJpaRepository jpa;

    public SessionRepositoryJpaAdapter(SpringDataSessionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Session session) {
        jpa.save(new SessionJpaEntity(session.tokenHash(), session.userId(), session.expiresAt()));
    }

    @Override
    public Optional<Session> findByTokenHash(String tokenHash) {
        return jpa.findById(tokenHash).map(e -> new Session(e.getTokenHash(), e.getUserId(), e.getExpiresAt()));
    }

    @Override
    public void delete(String tokenHash) {
        jpa.deleteById(tokenHash);
    }
}
