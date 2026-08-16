package com.peithyra.api.debate.internal.adapters.persistence;

import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.domain.Debate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDebateRepository implements DebateRepository {

    private final ConcurrentHashMap<UUID, Debate> debates = new ConcurrentHashMap<>();

    @Override
    public void save(Debate debate) {
        debates.put(debate.getId(), debate);
    }
}
