package com.peithyra.api.debate.support;

import com.peithyra.api.debate.internal.application.dto.PagedResult;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.domain.Debate;
import com.peithyra.api.debate.internal.domain.DebateStatus;

import java.util.Optional;
import java.util.Comparator;
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
        if (page < 0 || size < 1) {
            throw new IllegalArgumentException("Page must be non-negative and size must be positive");
        }
        var orderedDebates = debates.values().stream()
                .sorted(Comparator.comparing(Debate::getId))
                .toList();
        return new PagedResult<>(
                orderedDebates.stream().skip((long) page * size).limit(size).toList(),
                page,
                size,
                orderedDebates.size(),
                (int) ((orderedDebates.size() + (long) size - 1) / size)
        );
    }

    @Override
    public boolean existsByCreatorIdAndStatusIn(UUID creatorId, Set<DebateStatus> statuses) {
        return debates.values().stream()
                .anyMatch(debate -> debate.getParticipants().stream()
                        .anyMatch(participant -> participant.userId().equals(creatorId)) &&
                        statuses.contains(debate.getStatus()));
    }

    @Override
    public Optional<Debate> findById(UUID debateId) {
        return Optional.ofNullable(debates.get(debateId));
    }
}
