package com.peithyra.api.debate.internal.application.port.out;

import com.peithyra.api.debate.internal.application.dto.PagedResult;
import com.peithyra.api.debate.internal.domain.Debate;

public interface DebateRepository {
    void save(Debate debate);

    PagedResult<Debate> findDebatesPage(int page, int size);
}
