package com.peithyra.api.debate.internal.adapters.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataDebateRepository extends JpaRepository<DebateJpaEntity, UUID> {
}
