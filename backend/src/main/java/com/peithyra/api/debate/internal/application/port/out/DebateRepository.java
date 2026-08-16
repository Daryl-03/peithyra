package com.peithyra.api.debate.internal.application.port.out;

import com.peithyra.api.debate.internal.domain.Debate;

public interface DebateRepository {
    void save(Debate debate);
}
