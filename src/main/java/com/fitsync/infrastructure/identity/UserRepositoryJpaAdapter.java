package com.fitsync.infrastructure.identity;

import com.fitsync.domain.identity.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryJpaAdapter implements UserRepository {

    private final SpringDataUserJpaRepository jpa;

    public UserRepositoryJpaAdapter(SpringDataUserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public User save(User user) {
        return toDomain(jpa.save(toJpa(user)));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpa.findByEmail(email.value()).map(this::toDomain);
    }

    @Override
    public List<User> findAthletesOf(UUID coachId) {
        return jpa.findByCoachIdOrderByFullNameAsc(coachId).stream().map(this::toDomain).toList();
    }

    private UserJpaEntity toJpa(User u) {
        Brand b = u.brand().orElse(null);
        return new UserJpaEntity(u.id(), u.fullName(), u.email().value(), u.passwordHash(), u.role().name(),
            u.plan().name(), u.proSince().orElse(null), u.coachId().orElse(null),
            b == null ? null : b.name(), b == null ? null : b.color());
    }

    private User toDomain(UserJpaEntity e) {
        Brand brand = e.getBrandName() == null ? null : new Brand(e.getBrandName(), e.getBrandColor());
        return User.reconstruct(e.getId(), e.getFullName(), Email.of(e.getEmail()), e.getPasswordHash(),
            Role.valueOf(e.getRole()), SubscriptionPlan.valueOf(e.getPlan()), e.getProSince(), e.getCoachId(), brand);
    }
}
