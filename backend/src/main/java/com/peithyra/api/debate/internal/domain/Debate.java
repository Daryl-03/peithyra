package com.peithyra.api.debate.internal.domain;

import com.peithyra.api.debate.internal.domain.exceptions.DebateSideAlreadyTakenException;
import com.peithyra.api.debate.internal.domain.exceptions.ParticipantAlreadyJoinedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Debate {

    private final UUID id;
    private final String proposition;
    private final String description;
    private DebateStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant startedAt;
    private Instant endedAt;
    private final List<Participant> participants;

    public Debate(UUID id, String proposition, String description) {
        this.id = id;
        this.proposition = proposition;
        this.description = description;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.endedAt = null;
        this.status = DebateStatus.WAITING_FOR_OPPONENT;
        this.participants = new ArrayList<>();
    }

    public Debate(UUID id, String proposition, String description, DebateStatus status, List<Participant> participants, Instant createdAt, Instant startedAt, Instant endedAt, Instant updatedAt) {
        this.id = id;
        this.proposition = proposition;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.startedAt = startedAt;
        this.updatedAt = updatedAt;
        this.endedAt = endedAt;
        this.participants = new ArrayList<>(participants);
    }

    public void addParticipant(DebateSide side, UUID userId) {
        if (status != DebateStatus.WAITING_FOR_OPPONENT) {
            throw new IllegalStateException("Cannot add participant to a debate that is not waiting for an opponent.");
        }

        boolean alreadyIn = this.participants.stream()
                .anyMatch(p -> p.userId().equals(userId));

        if (alreadyIn) {
            throw new ParticipantAlreadyJoinedException("User is already a participant in this debate.");
        }

        boolean sideAlreadyTaken = this.participants.stream()
                .anyMatch(p -> p.side().equals(side));

        if (sideAlreadyTaken) {
            throw new DebateSideAlreadyTakenException("Debate side is already taken.");
        }

        this.participants.add(new Participant(userId, Instant.now(), side));

        if (this.participants.size() == 2) {
            this.status = DebateStatus.ONGOING;
            this.startedAt = Instant.now();
        }
    }

    public List<Participant> getParticipants() {
        return Collections.unmodifiableList(this.participants);
    }

    public UUID getId() {
        return id;
    }

    public String getProposition() {
        return proposition;
    }

    public String getDescription() {
        return description;
    }

    public DebateStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }
}
