package com.peithyra.api.debate.internal.adapters.persistence.jpa;

import com.peithyra.api.debate.internal.application.dto.PagedResult;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.domain.Debate;
import com.peithyra.api.debate.internal.domain.DebateStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class JpaDebateRepository implements DebateRepository {
    private final SpringDataDebateRepository repository;

    public JpaDebateRepository(SpringDataDebateRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Debate debate) {
        repository.save(DebateJpaEntity.fromDomain(debate));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<Debate> findDebatesPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var debates = repository.findAll(pageable);
        return new PagedResult<>(
                debates.getContent().stream().map(DebateJpaEntity::toDomain).toList(),
                debates.getNumber(),
                debates.getSize(),
                debates.getTotalElements(),
                debates.getTotalPages()
        );
    }

    @Override
    public boolean existsByParticipantIdAndStatusIn(UUID creatorId, Set<DebateStatus> statuses) {
        return repository.existsByParticipants_UserIdAndStatusIn(creatorId, statuses);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Debate> findById(UUID debateId) {
        return repository.findById(debateId).map(DebateJpaEntity::toDomain);
    }

    @Override
    public Optional<Debate> findByIdForUpdate(UUID debateId) {
        return repository.findDebateJpaEntityById(debateId).map(DebateJpaEntity::toDomain);
    }
}
