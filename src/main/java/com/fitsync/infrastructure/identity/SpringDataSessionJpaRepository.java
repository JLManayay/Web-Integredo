package com.fitsync.infrastructure.identity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSessionJpaRepository extends JpaRepository<SessionJpaEntity, String> {
}
