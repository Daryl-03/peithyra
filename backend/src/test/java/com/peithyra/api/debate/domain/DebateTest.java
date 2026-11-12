package com.peithyra.api.debate.domain;


import com.peithyra.api.debate.internal.domain.Debate;
import com.peithyra.api.debate.internal.domain.DebateFormat;
import com.peithyra.api.debate.internal.domain.DebateSide;
import com.peithyra.api.debate.internal.domain.DebateStatus;
import com.peithyra.api.debate.internal.domain.Participant;
import com.peithyra.api.debate.internal.domain.exceptions.DebateSideAlreadyTakenException;
import com.peithyra.api.debate.internal.domain.exceptions.ParticipantAlreadyJoinedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DebateTest {

    @Test
    void shouldBeWaitingForOpponentWhenCreated() {
        DebateFormat format = new DebateFormat(3, 60);
        Debate debate = new Debate(
                UUID.randomUUID(),
                "random prop",
                "",
                format,
                UUID.randomUUID()
        );

        assertAll(
                () -> assertEquals(DebateStatus.WAITING_FOR_OPPONENT, debate.getStatus()),
                () -> assertTrue(debate.getParticipants().isEmpty()),
                () -> assertNull(debate.getStartedAt()),
                () -> assertNotNull(debate.getCreatedAt())
        );
    }

    @Test
    void shouldPutDebateInWaitingForReadyWhenSecondParticipantJoins() {
        DebateFormat format = new DebateFormat(3, 60);
        Debate debate = new Debate(
                UUID.randomUUID(),
                "Naruto is the best manga",
                "",
                format,
                UUID.randomUUID()
        );

        debate.addParticipant(DebateSide.FOR, UUID.randomUUID());
        debate.addParticipant(DebateSide.AGAINST, UUID.randomUUID());

        assertAll(
                () -> assertEquals(DebateStatus.WAITING_FOR_READY, debate.getStatus()),
                () -> assertEquals(2, debate.getParticipants().size()),
                () -> assertNull(debate.getStartedAt()),
                () -> assertTrue(debate.getParticipants().stream().noneMatch(p -> p.isReady()))
        );
    }

    @Test
    void shouldNotAllowAddingParticipantWhenDebateIsNotWaitingForOpponent() {
        DebateFormat format = new DebateFormat(3, 60);
        Debate debate = new Debate(
                UUID.randomUUID(),
                "Training AI on copyrighted data is copyright infringement.",
                "",
                format,
                UUID.randomUUID()
        );

        debate.addParticipant(DebateSide.FOR, UUID.randomUUID());
        debate.addParticipant(DebateSide.AGAINST, UUID.randomUUID());

        var randomUserId = UUID.randomUUID();
        assertThrows(IllegalStateException.class, () -> debate.addParticipant(DebateSide.FOR, randomUserId));
    }

    @Test
    void shouldRejectParticipantWhenSideIsAlreadyTaken() {
        DebateFormat format = new DebateFormat(3, 60);
        Debate debate = new Debate(
                UUID.randomUUID(),
                "Training AI on copyrighted data is copyright infringement.",
                "A debate about copyright and artificial intelligence.",
                format,
                UUID.randomUUID()
        );

        debate.addParticipant(DebateSide.FOR, UUID.randomUUID());

        assertThrows(
                DebateSideAlreadyTakenException.class,
                () -> debate.addParticipant(DebateSide.FOR, UUID.randomUUID())
        );

        assertAll(
                () -> assertEquals(1, debate.getParticipants().size()),
                () -> assertEquals(
                        DebateStatus.WAITING_FOR_OPPONENT,
                        debate.getStatus()
                )
        );
    }

    @Test
    void shouldNotAllowSameParticipantTwice() {
        UUID userId = UUID.randomUUID();
        DebateFormat format = new DebateFormat(3, 60);
        Debate debate = new Debate(
                UUID.randomUUID(),
                "Training AI on copyrighted data is copyright infringement.",
                "A debate about copyright and artificial intelligence.",
                format,
                UUID.randomUUID()
        );

        debate.addParticipant(DebateSide.FOR, userId);

        assertThrows(
                ParticipantAlreadyJoinedException.class,
                () -> debate.addParticipant(DebateSide.AGAINST, userId)
        );
    }
    @Test
    void shouldMarkOnlyRequestedParticipantReadyAndPreserveTheirDetails() {
        Debate debate = debateAwaitingConfirmation(DebateStatus.WAITING_FOR_READY);
        var original = debate.getParticipants().getLast();
        var other = debate.getParticipants().getFirst();

        debate.markParticipantReady(original.userId());

        var ready = debate.getParticipants().getLast();
        assertAll(
                () -> assertTrue(ready.isReady()),
                () -> assertEquals(original.userId(), ready.userId()),
                () -> assertEquals(original.side(), ready.side()),
                () -> assertEquals(original.joinedAt(), ready.joinedAt()),
                () -> assertEquals(other, debate.getParticipants().getFirst()),
                () -> assertTrue(debate.getUpdatedAt().isAfter(Instant.EPOCH)),
                () -> assertEquals(DebateStatus.WAITING_FOR_READY, debate.getStatus()),
                () -> assertNull(debate.getStartedAt())
        );
    }

    @Test
    void shouldLeaveStateUnchangedWhenParticipantConfirmsTwice() {
        Debate debate = debateAwaitingConfirmation(DebateStatus.WAITING_FOR_READY);
        UUID userId = debate.getParticipants().getFirst().userId();
        debate.markParticipantReady(userId);
        var participants = List.copyOf(debate.getParticipants());
        var updatedAt = debate.getUpdatedAt();

        debate.markParticipantReady(userId);

        assertEquals(participants, debate.getParticipants());
        assertEquals(updatedAt, debate.getUpdatedAt());
    }

    @Test
    void shouldRejectReadinessFromNonParticipantWithoutChangingState() {
        Debate debate = debateAwaitingConfirmation(DebateStatus.WAITING_FOR_READY);
        var participants = List.copyOf(debate.getParticipants());

        assertThrows(IllegalArgumentException.class, () -> debate.markParticipantReady(UUID.randomUUID()));

        assertEquals(participants, debate.getParticipants());
        assertEquals(Instant.EPOCH, debate.getUpdatedAt());
    }

    @ParameterizedTest
    @EnumSource(value = DebateStatus.class, names = "WAITING_FOR_READY", mode = EnumSource.Mode.EXCLUDE)
    void shouldRejectReadinessOutsideConfirmationPhase(DebateStatus status) {
        Debate debate = debateAwaitingConfirmation(status);
        var participants = List.copyOf(debate.getParticipants());

        assertThrows(IllegalStateException.class,
                () -> debate.markParticipantReady(participants.getFirst().userId()));

        assertEquals(participants, debate.getParticipants());
        assertEquals(status, debate.getStatus());
        assertEquals(Instant.EPOCH, debate.getUpdatedAt());
    }

    @Test
    void shouldNotAutomaticallyStartWhenBothParticipantsAreReady() {
        Debate debate = debateAwaitingConfirmation(DebateStatus.WAITING_FOR_READY);
        for (var participant : List.copyOf(debate.getParticipants())) {
            debate.markParticipantReady(participant.userId());
        }

        assertTrue(debate.getParticipants().stream().allMatch(Participant::isReady));
        assertEquals(DebateStatus.WAITING_FOR_READY, debate.getStatus());
        assertNull(debate.getStartedAt());
    }

    private Debate debateAwaitingConfirmation(DebateStatus status) {
        UUID creatorId = UUID.randomUUID();
        var creator = new Participant(creatorId, Instant.EPOCH, DebateSide.FOR);
        var opponent = new Participant(UUID.randomUUID(), Instant.EPOCH, DebateSide.AGAINST);
        return new Debate.ReconstructBuilder(UUID.randomUUID(), "A proposition", "", Instant.EPOCH, creatorId)
                .withStatus(status)
                .withParticipants(status == DebateStatus.WAITING_FOR_OPPONENT
                        ? List.of(creator) : List.of(creator, opponent))
                .withUpdatedAt(Instant.EPOCH)
                .withFormat(new DebateFormat(3, 60))
                .build();
    }
}
