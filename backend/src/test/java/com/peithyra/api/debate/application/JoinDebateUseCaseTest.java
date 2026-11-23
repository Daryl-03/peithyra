package com.peithyra.api.debate.application;

import com.peithyra.api.debate.internal.application.exception.DebateAlreadyOngoingException;
import com.peithyra.api.debate.internal.application.exception.DebateNotFoundException;
import com.peithyra.api.debate.internal.domain.exceptions.DebateNotJoinableException;
import com.peithyra.api.debate.internal.application.port.in.JoinDebateUseCase;
import com.peithyra.api.debate.internal.domain.*;
import com.peithyra.api.debate.support.InMemoryDebateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.peithyra.api.debate.internal.application.dto.JoinDebateCommand;
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

        useCase.execute(new JoinDebateCommand(target.getId(), userId, creatorSide == DebateSide.FOR ? DebateSide.AGAINST : DebateSide.FOR));

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
                () -> useCase.execute(new JoinDebateCommand(UUID.randomUUID(), UUID.randomUUID(), DebateSide.FOR)));
    }

    @ParameterizedTest
    @EnumSource(value = DebateStatus.class, names = {"WAITING_FOR_OPPONENT", "WAITING_FOR_READY", "ONGOING"})
    void shouldRejectUserAlreadyParticipatingInActiveDebate(DebateStatus status) {
        UUID userId = UUID.randomUUID();
        repository.save(existingDebate(status, userId));
        Debate target = waitingDebate(DebateSide.FOR);

        assertThrows(DebateAlreadyOngoingException.class, () -> useCase.execute(new JoinDebateCommand(target.getId(), userId, DebateSide.AGAINST)));
        assertStillWaiting(target);
    }

    @ParameterizedTest
    @EnumSource(value = DebateStatus.class, names = {"DONE", "CANCELED"})
    void shouldAllowUserWithOnlyInactiveDebate(DebateStatus status) {
        UUID userId = UUID.randomUUID();
        repository.save(existingDebate(status, userId));
        Debate target = waitingDebate(DebateSide.FOR);

        useCase.execute(new JoinDebateCommand(target.getId(), userId, DebateSide.AGAINST));

        assertEquals(2, repository.findById(target.getId()).orElseThrow().getParticipants().size());
    }

    @Test
    void shouldRejectCreatorJoiningOwnDebate() {
        Debate target = waitingDebate(DebateSide.FOR);
        assertThrows(DebateAlreadyOngoingException.class,
                () -> useCase.execute(new JoinDebateCommand(target.getId(), target.getCreatorId(), DebateSide.AGAINST)));
        assertStillWaiting(target);
    }

    @ParameterizedTest
    @EnumSource(value = DebateStatus.class, names = {"WAITING_FOR_READY", "ONGOING", "DONE", "CANCELED"})
    void shouldRejectUnavailableDebateWithoutChangingParticipants(DebateStatus status) {
        Debate target = existingDebate(status, UUID.randomUUID());
        repository.save(target);
        var participants = List.copyOf(target.getParticipants());

        assertThrows(DebateNotJoinableException.class,
                () -> useCase.execute(new JoinDebateCommand(target.getId(), UUID.randomUUID(), DebateSide.AGAINST)));

        Debate saved = repository.findById(target.getId()).orElseThrow();
        assertEquals(participants, saved.getParticipants());
        assertEquals(status, saved.getStatus());
    }

    @Test
    void shouldJoinChosenTeamBeyondTwoParticipants() {
        UUID creatorId = UUID.randomUUID();
        Debate debate = new Debate(UUID.randomUUID(), "Team debate", "", new DebateFormat(3, 60, 2), creatorId);
        debate.addParticipant(DebateSide.FOR, creatorId);
        debate.addParticipant(DebateSide.AGAINST, UUID.randomUUID());
        repository.save(debate);
        UUID teammateId = UUID.randomUUID();

        useCase.execute(new JoinDebateCommand(debate.getId(), teammateId, DebateSide.FOR));

        Debate saved = repository.findById(debate.getId()).orElseThrow();
        assertEquals(3, saved.getParticipants().size());
        assertEquals(DebateStatus.WAITING_FOR_OPPONENT, saved.getStatus());
        assertTrue(saved.getParticipants().stream().anyMatch(p -> p.userId().equals(teammateId) && p.side() == DebateSide.FOR));

        useCase.execute(new JoinDebateCommand(debate.getId(), UUID.randomUUID(), DebateSide.AGAINST));
        assertEquals(DebateStatus.WAITING_FOR_READY, repository.findById(debate.getId()).orElseThrow().getStatus());
    }

    @Test
    void shouldNotSwitchSidesWhenChosenTeamIsFull() {
        UUID creatorId = UUID.randomUUID();
        Debate debate = new Debate(UUID.randomUUID(), "Team debate", "", new DebateFormat(3, 60, 2), creatorId);
        debate.addParticipant(DebateSide.FOR, creatorId);
        debate.addParticipant(DebateSide.FOR, UUID.randomUUID());
        repository.save(debate);

        assertThrows(com.peithyra.api.debate.internal.domain.exceptions.DebateSideFullException.class,
                () -> useCase.execute(new JoinDebateCommand(debate.getId(), UUID.randomUUID(), DebateSide.FOR)));

        assertEquals(debate.getParticipants(), repository.findById(debate.getId()).orElseThrow().getParticipants());
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
