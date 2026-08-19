package com.peithyra.api.debate.internal.application.dto;

import com.peithyra.api.debate.internal.domain.DebateSide;

import java.util.UUID;

public record CreateDebateCommand(
        String proposition,
        String description,
        UUID creatorId,
        DebateSide side
) {
}
