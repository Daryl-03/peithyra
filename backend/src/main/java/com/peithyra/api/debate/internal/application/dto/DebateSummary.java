package com.peithyra.api.debate.internal.application.dto;

import com.peithyra.api.debate.internal.domain.DebateStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DebateSummary(
        UUID id,
        String proposition,
        DebateStatus status,
        Instant createdAt,
        List<ParticipantSummary> participantViewList
) {
}
