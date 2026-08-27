package com.peithyra.api.debate.application;

import com.peithyra.api.debate.internal.application.dto.DebateSummary;
import com.peithyra.api.debate.internal.application.dto.ListDebateCommand;
import com.peithyra.api.debate.internal.application.dto.PagedResult;
import com.peithyra.api.debate.internal.application.port.in.ListDebatesUseCase;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.application.port.out.UserPort;
import com.peithyra.api.debate.internal.domain.Debate;
import com.peithyra.api.debate.internal.domain.DebateSide;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ListDebateUseCaseTest {

    private final ListDebatesUseCase listDebatesUseCase;
    private final DebateRepository debateRepository;
    private final UserPort userPort;

    public ListDebateUseCaseTest() {
        debateRepository = mock(DebateRepository.class);
        userPort = mock(UserPort.class);
        listDebatesUseCase = new ListDebatesUseCase(debateRepository, userPort);
    }

    @Test
    void shouldListDebatesWithParticipants() {
        UUID firstUserId = UUID.randomUUID();
        UUID secondUserId = UUID.randomUUID();

        Debate debate = new Debate(
                UUID.randomUUID(),
                "Moral and law are two separate matters",
                "A debate about moral and legal principles."
        );

        debate.addParticipant(DebateSide.FOR, firstUserId);
        debate.addParticipant(DebateSide.AGAINST, secondUserId);

        when(debateRepository.findDebatesPage(1, 5))
                .thenReturn(new PagedResult<>(
                        List.of(debate),
                        1,
                        5,
                        7,
                        2
                ));

        when(userPort.findUsernamesByIds(
                Set.of(firstUserId, secondUserId)
        )).thenReturn(Map.of(
                firstUserId, "alice",
                secondUserId, "bob"
        ));

        PagedResult<DebateSummary> result =
                listDebatesUseCase.execute(
                        new ListDebateCommand(1, 5)
                );

        DebateSummary summary = result.content().getFirst();

        assertAll(
                () -> assertEquals(1, result.page()),
                () -> assertEquals(5, result.size()),
                () -> assertEquals(7, result.totalElements()),
                () -> assertEquals(2, result.totalPages()),
                () -> assertEquals(debate.getId(), summary.id()),
                () -> assertEquals(2, summary.participantViewList().size()),
                () -> assertEquals(
                        "alice",
                        summary.participantViewList().getFirst().username()
                ),
                () -> assertEquals(
                        "bob",
                        summary.participantViewList().getLast().username()
                )
        );

        verify(debateRepository).findDebatesPage(1, 5);
        verify(userPort).findUsernamesByIds(
                Set.of(firstUserId, secondUserId)
        );
    }

    @Test
    void shouldUseUnknownWhenParticipantUsernameIsMissing() {
        UUID userId = UUID.randomUUID();

        Debate debate = new Debate(
                UUID.randomUUID(),
                "Moral and law are two separate matters",
                "A debate about moral and legal principles."
        );

        debate.addParticipant(DebateSide.FOR, userId);

        when(debateRepository.findDebatesPage(0, 10))
                .thenReturn(new PagedResult<>(
                        List.of(debate),
                        0,
                        10,
                        1,
                        1
                ));

        when(userPort.findUsernamesByIds(Set.of(userId)))
                .thenReturn(Map.of());

        PagedResult<DebateSummary> result =
                listDebatesUseCase.execute(
                        new ListDebateCommand(0, 10)
                );

        assertEquals(
                "unknown",
                result.content()
                        .getFirst()
                        .participantViewList()
                        .getFirst()
                        .username()
        );
    }
}
