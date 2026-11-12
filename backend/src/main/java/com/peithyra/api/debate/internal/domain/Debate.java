package com.peithyra.api.debate.internal.domain;

import com.peithyra.api.debate.internal.domain.exceptions.DebateSideFullException;
import com.peithyra.api.debate.internal.domain.exceptions.DebateNotJoinableException;
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
    private final UUID creatorId;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant startedAt;
    private Instant endedAt;
    private DebateFormat format;
    private final List<Participant> participants;

    public Debate(UUID id, String proposition, String description, DebateFormat format, UUID creatorId) {
        this.id = id;
        this.proposition = proposition;
        this.description = description;
        this.format = format;
        this.creatorId = creatorId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.endedAt = null;
        this.status = DebateStatus.WAITING_FOR_OPPONENT;
        this.participants = new ArrayList<>();
    }

    private Debate(UUID id, String proposition, String description, Instant createdAt, UUID creatorId) {
        this.id = id;
        this.proposition = proposition;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = Instant.now();
        this.endedAt = null;
        this.status = DebateStatus.WAITING_FOR_OPPONENT;
        this.participants = new ArrayList<>();
        this.creatorId = creatorId;
    }

    public void addParticipant(DebateSide side, UUID userId) {
        Objects.requireNonNull(side, "side is required");
        if (status != DebateStatus.WAITING_FOR_OPPONENT) {
            throw new DebateNotJoinableException("Debate is not available for joining");
        }

        boolean alreadyIn = this.participants.stream()
                .anyMatch(p -> p.userId().equals(userId));

        if (alreadyIn) {
            throw new ParticipantAlreadyJoinedException("User is already a participant in this debate.");
        }

        if (isSideFull(side)) {
            throw new DebateSideFullException("Debate side is full.");
        }

        this.participants.add(new Participant(userId, Instant.now(), side));

        refreshWaitingStatus();
        this.updatedAt = Instant.now();
    }

    public void cancel(UUID userId) {
        requireWaitingDebate();
        requireParticipant(userId);
        if (!creatorId.equals(userId)) {
            throw new IllegalArgumentException("Only the creator can cancel this debate.");
        }

        status = DebateStatus.CANCELED;
        updatedAt = Instant.now();
    }

    public void removeParticipant(UUID userId) {
        requireWaitingDebate();
        requireParticipant(userId);
        if (creatorId.equals(userId)) {
            throw new IllegalArgumentException("The creator must cancel the debate instead of leaving.");
        }

        participants.removeIf(participant -> participant.userId().equals(userId));
        participants.replaceAll(participant -> new Participant(
                participant.userId(), participant.joinedAt(), participant.side(), false
        ));
        refreshWaitingStatus();
        updatedAt = Instant.now();
    }

    private void requireWaitingDebate() {
        if (status != DebateStatus.WAITING_FOR_OPPONENT && status != DebateStatus.WAITING_FOR_READY) {
            throw new IllegalStateException("Participants can only leave or cancel a debate before it starts.");
        }
    }

    private void requireParticipant(UUID userId) {
        if (participants.stream().noneMatch(participant -> participant.userId().equals(userId))) {
            throw new IllegalArgumentException("User is not a participant in this debate.");
        }
    }

    public void markParticipantReady(UUID userId) {
        if (status != DebateStatus.WAITING_FOR_READY) {
            throw new IllegalStateException(
                    "Debate is not waiting for participants to be ready."
            );
        }

        for (int i = 0; i < participants.size(); i++) {
            Participant participant = participants.get(i);

            if (participant.userId().equals(userId)) {
                if (participant.isReady()) {
                    return;
                }

                participants.set(i, new Participant(
                        participant.userId(),
                        participant.joinedAt(),
                        participant.side(),
                        true
                ));
                updatedAt = Instant.now();
                return;
            }
        }

        throw new IllegalArgumentException(
                "User is not a participant in this debate."
        );
    }

//    public void startDebate() {
//        if (this.status != DebateStatus.WAITING_FOR_READY) {
//            throw new IllegalStateException("Debate cannot be started unless it is waiting for ready.");
//        }
//
//        boolean allReady = this.status == DebateStatus.WAITING_FOR_READY && this.participants.stream().allMatch(Participant::isReady);
//
//        if (!allReady) {
//            throw new IllegalStateException("Cannot start debate until all participants are ready.");
//        }
//
//        this.status = DebateStatus.ONGOING;
//        this.startedAt = Instant.now();
//    }

    private boolean isFull() {
        return this.participants.size() >= format.participantsPerSide() * 2;
    }

    private boolean isSideFull(DebateSide side) {
        return participants.stream().filter(p -> p.side() == side).count() >= format.participantsPerSide();
    }

    private void refreshWaitingStatus() {
        status = isFull() ? DebateStatus.WAITING_FOR_READY : DebateStatus.WAITING_FOR_OPPONENT;
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

    public UUID getCreatorId() {
        return creatorId;
    }

    public static class ReconstructBuilder {
        private final UUID id;
        private final String proposition;
        private final String description;
        private final Instant createdAt;
        private final UUID creatorId;
        private DebateStatus status;
        private List<Participant> participants;
        private Instant startedAt;
        private Instant endedAt;
        private Instant updatedAt;
        private DebateFormat format;

        public ReconstructBuilder(UUID id, String proposition, String description, Instant createdAt, UUID creatorId) {
            this.id = id;
            this.proposition = proposition;
            this.description = description;
            this.createdAt = createdAt;
            this.creatorId = creatorId;
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

            Debate debate = new Debate(id, proposition, description, createdAt, creatorId);
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
