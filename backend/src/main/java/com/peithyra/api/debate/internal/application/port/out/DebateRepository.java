package com.peithyra.api.debate.internal.application.port.out;

import com.peithyra.api.debate.internal.application.dto.PagedResult;
import com.peithyra.api.debate.internal.domain.Debate;
import com.peithyra.api.debate.internal.domain.DebateStatus;

import java.util.Set;
import java.util.UUID;

public interface DebateRepository {
    void save(Debate debate);

    PagedResult<Debate> findDebatesPage(int page, int size);

    boolean existsByCreatorIdAndStatusIn(UUID creatorId, Set<DebateStatus> statuses);
}
