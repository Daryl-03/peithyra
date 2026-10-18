package com.peithyra.api.debate.internal.domain;

import com.peithyra.api.debate.internal.domain.exceptions.DebateSideAlreadyTakenException;
import com.peithyra.api.debate.internal.domain.exceptions.ParticipantAlreadyJoinedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    private DebateFormat format;
    private final List<Participant> participants;

    public Debate(UUID id, String proposition, String description, DebateFormat format) {
        this.id = id;
        this.proposition = proposition;
        this.description = description;
        this.format = format;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.endedAt = null;
        this.status = DebateStatus.WAITING_FOR_OPPONENT;
        this.participants = new ArrayList<>();
    }
    public Debate(UUID id, String proposition, String description, Instant createdAt){
        this.id = id;
        this.proposition = proposition;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = Instant.now();
        this.endedAt = null;
        this.status = DebateStatus.WAITING_FOR_OPPONENT;
        this.participants = new ArrayList<>();
    }

//    public Debate(UUID id, String proposition, String description, DebateStatus status, List<Participant> participants, Instant createdAt, Instant startedAt, Instant endedAt, Instant updatedAt) {
//        this.id = id;
//        this.proposition = proposition;
//        this.description = description;
//        this.status = status;
//        this.createdAt = createdAt;
//        this.startedAt = startedAt;
//        this.updatedAt = updatedAt;
//        this.endedAt = endedAt;
//        this.participants = new ArrayList<>(participants);
//    }

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

    public DebateFormat getFormat() {
        return format;
    }

    public static class ReconstructBuilder {
        private final UUID id;
        private final String proposition;
        private final String description;
        private final Instant createdAt;
        private DebateStatus status;
        private List<Participant> participants;
        private Instant startedAt;
        private Instant endedAt;
        private Instant updatedAt;
        private DebateFormat format;

        public ReconstructBuilder(UUID id, String proposition, String description, Instant createdAt) {
            this.id = id;
            this.proposition = proposition;
            this.description = description;
            this.createdAt = createdAt;
        }

        public ReconstructBuilder withStatus(DebateStatus status) {
            this.status = status;
            return this;
        }

        public ReconstructBuilder withParticipants(List<Participant> participants) {
            this.participants = List.copyOf(participants);
            return this;
        }

        public ReconstructBuilder withStartedAt(Instant startedAt) {
            this.startedAt = startedAt;
            return this;
        }

        public ReconstructBuilder withEndedAt(Instant endedAt) {
            this.endedAt = endedAt;
            return this;
        }

        public ReconstructBuilder withUpdatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ReconstructBuilder withFormat(DebateFormat format) {
            this.format = format;
            return this;
        }

        public Debate build() {
            Objects.requireNonNull(id, "id is required for reconstruction");
            Objects.requireNonNull(proposition, "proposition is required for reconstruction");
            Objects.requireNonNull(createdAt, "createdAt is required for reconstruction");
            Objects.requireNonNull(status, "status is required for reconstruction");
            Objects.requireNonNull(participants, "participants is required for reconstruction");
            Objects.requireNonNull(updatedAt, "updatedAt is required for reconstruction");
            Objects.requireNonNull(format, "format is required for reconstruction");

            Debate debate = new Debate(id, proposition, description, createdAt);
            debate.status = this.status;
            debate.participants.addAll(this.participants);
            debate.startedAt = this.startedAt;
            debate.endedAt = this.endedAt;
            debate.updatedAt = this.updatedAt;
            debate.format = this.format;
            return debate;
        }

    }
}
