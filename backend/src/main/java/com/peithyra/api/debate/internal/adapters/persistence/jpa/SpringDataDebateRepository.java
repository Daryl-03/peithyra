package com.peithyra.api.debate.internal.adapters.persistence.jpa;

import com.peithyra.api.debate.internal.domain.DebateStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataDebateRepository extends JpaRepository<DebateJpaEntity, UUID> {

    boolean existsByParticipants_UserIdAndStatusIn(
            UUID userId,
            Collection<DebateStatus> statuses
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DebateJpaEntity> findDebateJpaEntityById(UUID id);
}
