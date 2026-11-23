package com.peithyra.api.debate.internal.application.dto;

import com.peithyra.api.debate.internal.domain.DebateSide;

import java.util.UUID;

public record ParticipantDetails(
        UUID id,
        String username,
        DebateSide side,
        boolean isReady
) {
}
