package com.peithyra.api.debate.internal.application.dto;

import com.peithyra.api.debate.internal.domain.DebateSide;
import com.peithyra.api.debate.internal.domain.DebateStatus;

import java.time.Instant;
import java.util.UUID;

public record CreateDebateResponse(
        UUID id,
        String proposition,
        String description,
        DebateStatus status,
        DebateSide side,
        Instant createdAt
) {
}
