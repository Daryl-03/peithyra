package com.peithyra.api.debate.domain;


import com.peithyra.api.debate.internal.domain.Debate;
import com.peithyra.api.debate.internal.domain.DebateSide;
import com.peithyra.api.debate.internal.domain.DebateStatus;
import com.peithyra.api.debate.internal.domain.exceptions.DebateSideAlreadyTakenException;
import com.peithyra.api.debate.internal.domain.exceptions.ParticipantAlreadyJoinedException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DebateTest {

    @Test
    void shouldBeWaitingForOpponentWhenCreated() {
        Debate debate = new Debate(
                UUID.randomUUID(),
                "random prop",
                ""
        );

        assertAll(
                () -> assertEquals(DebateStatus.WAITING_FOR_OPPONENT, debate.getStatus()),
                () -> assertTrue(debate.getParticipants().isEmpty()),
                () -> assertNull(debate.getStartedAt()),
                () -> assertNotNull(debate.getCreatedAt())
        );
    }

    @Test
    void shouldStartDebateWhenSecondParticipantJoins() {
        Debate debate = new Debate(
                UUID.randomUUID(),
                "Naruto is the best manga",
                ""
        );

        debate.addParticipant(DebateSide.FOR, UUID.randomUUID());
        debate.addParticipant(DebateSide.AGAINST, UUID.randomUUID());

        assertAll(
                () -> assertEquals(DebateStatus.ONGOING, debate.getStatus()),
                () -> assertEquals(2, debate.getParticipants().size()),
                () -> assertNotNull(debate.getStartedAt())
        );
    }

    @Test
    void shouldNotAllowAddingParticipantWhenDebateIsNotWaitingForOpponent() {
        Debate debate = new Debate(
                UUID.randomUUID(),
                "Training AI on copyrighted data is copyright infringement.",
                ""
        );

        debate.addParticipant(DebateSide.FOR, UUID.randomUUID());
        debate.addParticipant(DebateSide.AGAINST, UUID.randomUUID());

        assertThrows(IllegalStateException.class, () -> {
            debate.addParticipant(DebateSide.FOR, UUID.randomUUID());
        });
    }

    @Test
    void shouldRejectParticipantWhenSideIsAlreadyTaken() {
        Debate debate = new Debate(
                UUID.randomUUID(),
                "Training AI on copyrighted data is copyright infringement.",
                "A debate about copyright and artificial intelligence."
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
        Debate debate = new Debate(
                UUID.randomUUID(),
                "Training AI on copyrighted data is copyright infringement.",
                "A debate about copyright and artificial intelligence."
        );

        debate.addParticipant(DebateSide.FOR, userId);

        assertThrows(
                ParticipantAlreadyJoinedException.class,
                () -> debate.addParticipant(DebateSide.AGAINST, userId)
        );
    }
}
