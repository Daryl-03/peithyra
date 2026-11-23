package com.peithyra.api.debate.application;

import com.peithyra.api.debate.internal.application.exception.DebateNotFoundException;
import com.peithyra.api.debate.internal.application.port.in.LeaveDebateUseCase;
import com.peithyra.api.debate.internal.domain.*;
import com.peithyra.api.debate.support.InMemoryDebateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LeaveDebateUseCaseTest {
    private final InMemoryDebateRepository repository = new InMemoryDebateRepository();
    private final LeaveDebateUseCase useCase = new LeaveDebateUseCase(repository);
    private final UUID creatorId = UUID.randomUUID();
    private final UUID opponentId = UUID.randomUUID();

    @ParameterizedTest
    @EnumSource(value = DebateStatus.class, names = {"WAITING_FOR_OPPONENT", "WAITING_FOR_READY"})
    void shouldSaveCancellationWhenCreatorLeaves(DebateStatus status) {
        Debate debate = saveDebate(status);
        var participants = List.copyOf(debate.getParticipants());

        useCase.execute(debate.getId(), creatorId);

        Debate saved = repository.findById(debate.getId()).orElseThrow();
        assertEquals(DebateStatus.CANCELED, saved.getStatus());
        assertEquals(participants, saved.getParticipants());
        assertEquals(creatorId, saved.getCreatorId());
    }

    @Test
    void shouldSaveOpponentDepartureAndResetCreatorReadiness() {
        Debate debate = saveDebate(DebateStatus.WAITING_FOR_READY);

        useCase.execute(debate.getId(), opponentId);

        Debate saved = repository.findById(debate.getId()).orElseThrow();
        assertEquals(DebateStatus.WAITING_FOR_OPPONENT, saved.getStatus());
        assertEquals(1, saved.getParticipants().size());
        assertEquals(creatorId, saved.getParticipants().getFirst().userId());
        assertFalse(saved.getParticipants().getFirst().isReady());
        assertNull(saved.getStartedAt());
    }

    @Test
    void shouldRejectMissingDebate() {
        assertThrows(DebateNotFoundException.class,
                () -> useCase.execute(UUID.randomUUID(), creatorId));
    }

    @Test
    void shouldRejectNonParticipantWithoutChangingStoredDebate() {
        Debate debate = saveDebate(DebateStatus.WAITING_FOR_READY);
        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(debate.getId(), UUID.randomUUID()));
        assertUnchanged(debate);
    }

    @ParameterizedTest
    @EnumSource(value = DebateStatus.class, names = {"ONGOING", "DONE", "CANCELED"})
    void shouldRejectBothParticipantsOutsideWaitingPhase(DebateStatus status) {
        Debate debate = saveDebate(status);
        assertThrows(IllegalStateException.class, () -> useCase.execute(debate.getId(), creatorId));
        assertThrows(IllegalStateException.class, () -> useCase.execute(debate.getId(), opponentId));
        assertUnchanged(debate);
    }

    private void assertUnchanged(Debate original) {
        Debate saved = repository.findById(original.getId()).orElseThrow();
        assertEquals(original.getStatus(), saved.getStatus());
        assertEquals(original.getParticipants(), saved.getParticipants());
        assertEquals(original.getUpdatedAt(), saved.getUpdatedAt());
    }

    private Debate saveDebate(DebateStatus status) {
        var creator = new Participant(creatorId, Instant.EPOCH, DebateSide.AGAINST,
                status != DebateStatus.WAITING_FOR_OPPONENT);
        var opponent = new Participant(opponentId, Instant.EPOCH, DebateSide.FOR, true);
        Debate debate = new Debate.ReconstructBuilder(UUID.randomUUID(), "A proposition", "", Instant.EPOCH, creatorId)
                .withStatus(status)
                .withParticipants(status == DebateStatus.WAITING_FOR_OPPONENT ? List.of(creator) : List.of(creator, opponent))
                .withUpdatedAt(Instant.EPOCH)
                .withFormat(new DebateFormat(3, 60))
                .build();
        repository.save(debate);
        return debate;
    }
}
