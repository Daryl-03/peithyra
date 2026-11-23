package com.peithyra.api.debate.internal.adapters.web.dto;

import com.peithyra.api.debate.internal.application.dto.JoinDebateCommand;
import com.peithyra.api.debate.internal.domain.DebateSide;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record JoinDebateRequest(@NotNull DebateSide side) {
    public JoinDebateCommand toCommand(UUID debateId, UUID userId) {
        return new JoinDebateCommand(debateId, userId, side);
    }
}
