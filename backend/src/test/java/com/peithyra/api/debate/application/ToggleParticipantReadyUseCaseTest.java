package com.peithyra.api.debate.application;

import com.peithyra.api.debate.internal.application.exception.DebateNotFoundException;
import com.peithyra.api.debate.internal.application.port.in.ToggleParticipantReadyUseCase;
import com.peithyra.api.debate.support.InMemoryDebateRepository;
import com.peithyra.api.debate.internal.domain.*;
import com.peithyra.api.debate.internal.domain.exceptions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ToggleParticipantReadyUseCaseTest {
    private final InMemoryDebateRepository repository = new InMemoryDebateRepository();
    private final ToggleParticipantReadyUseCase useCase = new ToggleParticipantReadyUseCase(repository);
    private final UUID userId = UUID.randomUUID();

    @Test
    void shouldSaveReadinessChangesInBothDirections() {
        Debate debate = givenDebate(DebateStatus.WAITING_FOR_READY);
        Participant opponent = debate.getParticipants().getLast();
        useCase.execute(debate.getId(), userId);

        Debate ready = repository.findById(debate.getId()).orElseThrow();
        assertTrue(ready.getParticipants().getFirst().isReady());
        assertEquals(opponent, ready.getParticipants().getLast());
        assertTrue(ready.getUpdatedAt().isAfter(Instant.EPOCH));
        assertEquals(DebateStatus.WAITING_FOR_READY, ready.getStatus());

        useCase.execute(debate.getId(), userId);

        Debate notReady = repository.findById(debate.getId()).orElseThrow();
        assertFalse(notReady.getParticipants().getFirst().isReady());
        assertEquals(opponent, notReady.getParticipants().getLast());
        assertEquals(DebateStatus.WAITING_FOR_READY, notReady.getStatus());
    }

    @Test
    void shouldRejectMissingDebate() {
        UUID id = UUID.randomUUID();
        assertThrows(DebateNotFoundException.class, () -> useCase.execute(id, userId));
        assertTrue(repository.findById(id).isEmpty());
    }

    @Test
    void shouldRejectNonParticipant() {
        Debate debate = givenDebate(DebateStatus.WAITING_FOR_READY);
        assertThrows(ParticipantNotFoundException.class,
                () -> useCase.execute(debate.getId(), UUID.randomUUID()));
        assertStoredDebateUnchanged(debate);
    }

    @ParameterizedTest
    @EnumSource(value = DebateStatus.class, names = "WAITING_FOR_READY", mode = EnumSource.Mode.EXCLUDE)
    void shouldRejectOtherStatuses(DebateStatus status) {
        Debate debate = givenDebate(status);
        assertThrows(DebateNotWaitingForReadyException.class,
                () -> useCase.execute(debate.getId(), userId));
        assertStoredDebateUnchanged(debate);
    }

    private void assertStoredDebateUnchanged(Debate original) {
        Debate saved = repository.findById(original.getId()).orElseThrow();
        assertEquals(original.getParticipants(), saved.getParticipants());
        assertEquals(original.getStatus(), saved.getStatus());
        assertEquals(original.getUpdatedAt(), saved.getUpdatedAt());
    }

    private Debate givenDebate(DebateStatus status) {
        Debate debate = new Debate.ReconstructBuilder(UUID.randomUUID(), "Proposition", "", Instant.EPOCH, userId)
                .withStatus(status)
                .withParticipants(List.of(new Participant(userId, Instant.EPOCH, DebateSide.FOR, false),
                        new Participant(UUID.randomUUID(), Instant.EPOCH, DebateSide.AGAINST, true)))
                .withUpdatedAt(Instant.EPOCH)
                .withFormat(new DebateFormat(3, 60))
                .build();
        repository.save(debate);
        return debate;
    }
}
