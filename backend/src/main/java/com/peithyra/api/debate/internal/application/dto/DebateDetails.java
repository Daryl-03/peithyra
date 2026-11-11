package com.peithyra.api.debate.internal.application.dto;

import com.peithyra.api.debate.internal.domain.DebateFormat;
import com.peithyra.api.debate.internal.domain.DebateStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DebateDetails(
        UUID id,
        String proposition,
        String description,
        DebateStatus status,
        DebateFormat format,
        UUID creatorId,
        Instant createdAt,
        List<ParticipantSummary> participants
) {
}
