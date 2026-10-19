package com.peithyra.api.debate.internal.adapters.persistence.jpa;

import com.peithyra.api.debate.internal.domain.DebateStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.UUID;

public interface SpringDataDebateRepository extends JpaRepository<DebateJpaEntity, UUID> {

    boolean existsByParticipants_UserIdAndStatusIn(
            UUID userId,
            Collection<DebateStatus> statuses
    );
}
