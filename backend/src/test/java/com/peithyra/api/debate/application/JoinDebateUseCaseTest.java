package com.peithyra.api.debate.application;

import com.peithyra.api.debate.internal.application.exception.DebateAlreadyOngoingException;
import com.peithyra.api.debate.internal.application.exception.DebateNotFoundException;
import com.peithyra.api.debate.internal.application.exception.DebateNotJoinableException;
import com.peithyra.api.debate.internal.application.port.in.JoinDebateUseCase;
import com.peithyra.api.debate.internal.domain.*;
import com.peithyra.api.debate.support.InMemoryDebateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JoinDebateUseCaseTest {
    private final InMemoryDebateRepository repository = new InMemoryDebateRepository();
    private final JoinDebateUseCase useCase = new JoinDebateUseCase(repository);

    @ParameterizedTest
    @EnumSource(DebateSide.class)
    void shouldJoinAvailableSideAndSaveWaitingForReady(DebateSide creatorSide) {
        Debate target = waitingDebate(creatorSide);
        UUID userId = UUID.randomUUID();

        useCase.execute(target.getId(), userId);

        Debate saved = repository.findById(target.getId()).orElseThrow();
        Participant joined = saved.getParticipants().stream()
                .filter(p -> p.userId().equals(userId)).findFirst().orElseThrow();
        assertAll(
                () -> assertEquals(2, saved.getParticipants().size()),
                () -> assertEquals(creatorSide == DebateSide.FOR ? DebateSide.AGAINST : DebateSide.FOR, joined.side()),
                () -> assertFalse(joined.isReady()),
                () -> assertEquals(target.getCreatorId(), saved.getCreatorId()),
                () -> assertEquals(DebateStatus.WAITING_FOR_READY, saved.getStatus()),
                () -> assertNull(saved.getStartedAt())
        );
    }

    @Test
    void shouldRejectMissingDebate() {
        assertThrows(DebateNotFoundException.class,
                () -> useCase.execute(UUID.randomUUID(), UUID.randomUUID()));
    }

    @ParameterizedTest
    @EnumSource(value = DebateStatus.class, names = {"WAITING_FOR_OPPONENT", "WAITING_FOR_READY", "ONGOING"})
    void shouldRejectUserAlreadyParticipatingInActiveDebate(DebateStatus status) {
        UUID userId = UUID.randomUUID();
        repository.save(existingDebate(status, userId));
        Debate target = waitingDebate(DebateSide.FOR);

        assertThrows(DebateAlreadyOngoingException.class, () -> useCase.execute(target.getId(), userId));
        assertStillWaiting(target);
    }

    @ParameterizedTest
    @EnumSource(value = DebateStatus.class, names = {"DONE", "CANCELED"})
    void shouldAllowUserWithOnlyInactiveDebate(DebateStatus status) {
        UUID userId = UUID.randomUUID();
        repository.save(existingDebate(status, userId));
        Debate target = waitingDebate(DebateSide.FOR);

        useCase.execute(target.getId(), userId);

        assertEquals(2, repository.findById(target.getId()).orElseThrow().getParticipants().size());
    }

    @Test
    void shouldRejectCreatorJoiningOwnDebate() {
        Debate target = waitingDebate(DebateSide.FOR);
        assertThrows(DebateAlreadyOngoingException.class,
                () -> useCase.execute(target.getId(), target.getCreatorId()));
        assertStillWaiting(target);
    }

    @ParameterizedTest
    @EnumSource(value = DebateStatus.class, names = {"WAITING_FOR_READY", "ONGOING", "DONE", "CANCELED"})
    void shouldRejectUnavailableDebateWithoutChangingParticipants(DebateStatus status) {
        Debate target = existingDebate(status, UUID.randomUUID());
        repository.save(target);
        var participants = List.copyOf(target.getParticipants());

        assertThrows(DebateNotJoinableException.class,
                () -> useCase.execute(target.getId(), UUID.randomUUID()));

        Debate saved = repository.findById(target.getId()).orElseThrow();
        assertEquals(participants, saved.getParticipants());
        assertEquals(status, saved.getStatus());
    }

    private Debate waitingDebate(DebateSide side) {
        UUID creatorId = UUID.randomUUID();
        Debate debate = new Debate(UUID.randomUUID(), "A proposition", "", new DebateFormat(3, 60), creatorId);
        debate.addParticipant(side, creatorId);
        repository.save(debate);
        return debate;
    }

    private Debate existingDebate(DebateStatus status, UUID userId) {
        UUID creatorId = status == DebateStatus.WAITING_FOR_OPPONENT ? userId : UUID.randomUUID();
        Instant now = Instant.now();
        var participants = status == DebateStatus.WAITING_FOR_OPPONENT
                ? List.of(new Participant(userId, now, DebateSide.FOR))
                : List.of(new Participant(creatorId, now, DebateSide.FOR),
                        new Participant(userId, now, DebateSide.AGAINST));
        return new Debate.ReconstructBuilder(UUID.randomUUID(), "Existing debate", "", now, creatorId)
                .withStatus(status)
                .withParticipants(participants)
                .withUpdatedAt(now)
                .withFormat(new DebateFormat(3, 60))
                .build();
    }

    private void assertStillWaiting(Debate target) {
        Debate saved = repository.findById(target.getId()).orElseThrow();
        assertEquals(1, saved.getParticipants().size());
        assertEquals(DebateStatus.WAITING_FOR_OPPONENT, saved.getStatus());
    }
}
