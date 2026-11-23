package com.peithyra.api.debate.internal.application.dto;

import com.peithyra.api.debate.internal.domain.DebateSide;

import java.util.Objects;
import java.util.UUID;

public record JoinDebateCommand(UUID debateId, UUID userId, DebateSide side) {
    public JoinDebateCommand {
        Objects.requireNonNull(debateId, "debateId is required");
        Objects.requireNonNull(userId, "userId is required");
        Objects.requireNonNull(side, "side is required");
    }
}
