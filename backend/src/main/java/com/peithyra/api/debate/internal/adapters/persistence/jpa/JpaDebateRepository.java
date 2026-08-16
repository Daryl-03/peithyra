package com.peithyra.api.debate.internal.adapters.persistence.jpa;

import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.domain.Debate;
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
}
