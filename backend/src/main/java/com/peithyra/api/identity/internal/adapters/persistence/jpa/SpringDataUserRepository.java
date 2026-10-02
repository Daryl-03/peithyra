package com.peithyra.api.identity.internal.adapters.persistence.jpa;

import com.peithyra.api.identity.internal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByExternalId(String externalId);
}
