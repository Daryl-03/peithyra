package com.peithyra.api.debate.internal.adapters.persistence.jpa;


import com.peithyra.api.debate.internal.domain.Debate;
import com.peithyra.api.debate.internal.domain.DebateStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "debates")
public class DebateJpaEntity {
    @Id
    private UUID id;

    private String proposition;
    private String description;
    @Enumerated(EnumType.STRING)
    private DebateStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant startedAt;
    private Instant endedAt;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "debate_participants",
            joinColumns = @JoinColumn(name = "debate_id")
    )
    @BatchSize(size = 50)
    private List<ParticipantJpaEmbeddable> participants;

    public DebateJpaEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DebateJpaEntity that = (DebateJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static DebateJpaEntity fromDomain(Debate debate) {
        DebateJpaEntity entity = new DebateJpaEntity();

        entity.id = debate.getId();
        entity.proposition = debate.getProposition();
        entity.description = debate.getDescription();
        entity.status = debate.getStatus();
        entity.createdAt = debate.getCreatedAt();
        entity.updatedAt = debate.getUpdatedAt();
        entity.startedAt = debate.getStartedAt();
        entity.endedAt = debate.getEndedAt();

        entity.participants = debate.getParticipants().stream()
                .map(ParticipantJpaEmbeddable::fromDomain)
                .toList();

        return entity;
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

    public List<ParticipantJpaEmbeddable> getParticipants() {
        return participants;
    }

    public static Debate toDomain(DebateJpaEntity entity) {

        return new Debate(
                entity.id,
                entity.proposition,
                entity.description,
                entity.status,
                entity.participants.stream().map(ParticipantJpaEmbeddable::toDomain).toList(),
                entity.createdAt,
                entity.startedAt,
                entity.endedAt,
                entity.updatedAt
        );
    }
}
