package com.fitsync.infrastructure.identity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);

    List<UserJpaEntity> findByCoachIdOrderByFullNameAsc(UUID coachId);
}
