package com.peithyra.api.identity.internal.adapters.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {
    boolean existsByUsernameIgnoreCase(String username);

    Optional<UserJpaEntity> findByExternalId(String externalId);
}
