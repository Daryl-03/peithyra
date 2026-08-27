package com.peithyra.api.debate.internal.domain;

import java.time.Instant;
import java.util.UUID;

public record Participant(UUID userId, Instant joinedAt, DebateSide side) {
}
