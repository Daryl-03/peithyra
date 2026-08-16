package com.peithyra.api.debate.internal.application.dto;

import com.peithyra.api.debate.internal.domain.DebateSide;

public record CreateDebateCommand(
        String proposition,
        String description,
        DebateSide side
) {
}
