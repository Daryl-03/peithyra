package com.peithyra.api.debate.internal.adapters.persistence;

import com.peithyra.api.debate.internal.application.dto.PagedResult;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.domain.Debate;
import com.peithyra.api.debate.internal.domain.DebateStatus;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDebateRepository implements DebateRepository {

    private final ConcurrentHashMap<UUID, Debate> debates = new ConcurrentHashMap<>();

    @Override
    public void save(Debate debate) {
        debates.put(debate.getId(), debate);
    }

    @Override
    public PagedResult<Debate> findDebatesPage(int page, int size) {
        return null;
    }

    @Override
    public boolean existsByCreatorIdAndStatusIn(UUID creatorId, Set<DebateStatus> statuses) {
        return false;
    }
}
