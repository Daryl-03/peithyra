package com.peithyra.api.debate.internal.adapters.persistence.jpa;

import com.peithyra.api.debate.internal.domain.DebateSide;
import com.peithyra.api.debate.internal.domain.Participant;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.Instant;
import java.util.UUID;

@Embeddable
public class ParticipantJpaEmbeddable {

    private UUID userId;
    private Instant joinedAt;
    @Enumerated(EnumType.STRING)
    private DebateSide side;

    protected ParticipantJpaEmbeddable() {}

    protected ParticipantJpaEmbeddable(UUID userId, Instant joinedAt, DebateSide side) {
        this.userId = userId;
        this.joinedAt = joinedAt;
        this.side = side;
    }

    public static ParticipantJpaEmbeddable fromDomain(Participant participant) {
        return new ParticipantJpaEmbeddable(
                participant.userId(),
                participant.joinedAt(),
                participant.side()
        );
    }

}
