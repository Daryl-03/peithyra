package com.peithyra.api.debate.internal.adapters.persistence.jpa;

import com.peithyra.api.debate.internal.application.dto.PagedResult;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.domain.Debate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
}
