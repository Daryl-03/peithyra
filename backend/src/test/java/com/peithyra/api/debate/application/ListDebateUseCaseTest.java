package com.peithyra.api.debate.application;

import com.peithyra.api.debate.support.InMemoryDebateRepository;
import com.peithyra.api.debate.support.InMemoryUserAdapter;
import com.peithyra.api.debate.internal.application.dto.DebateSummary;
import com.peithyra.api.debate.internal.application.dto.ListDebateCommand;
import com.peithyra.api.debate.internal.application.dto.PagedResult;
import com.peithyra.api.debate.internal.application.port.in.ListDebatesUseCase;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.domain.Debate;
import com.peithyra.api.debate.internal.domain.DebateFormat;
import com.peithyra.api.debate.internal.domain.DebateSide;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListDebateUseCaseTest {

    private final ListDebatesUseCase listDebatesUseCase;
    private final DebateRepository debateRepository;
    private final InMemoryUserAdapter userPort;

    public ListDebateUseCaseTest() {
        debateRepository = new InMemoryDebateRepository();
        userPort = new InMemoryUserAdapter();
        listDebatesUseCase = new ListDebatesUseCase(debateRepository, userPort);
    }

    @Test
    void shouldListDebatesWithParticipants() {
        UUID firstUserId = UUID.randomUUID();
        UUID secondUserId = UUID.randomUUID();
        DebateFormat format = new DebateFormat(3, 60);

        Debate debate = new Debate(
                new UUID(0, 6),
                "Moral and law are two separate matters",
                "A debate about moral and legal principles.",
                format
        );

        debate.addParticipant(DebateSide.FOR, firstUserId);
        debate.addParticipant(DebateSide.AGAINST, secondUserId);

        for (int id = 1; id <= 7; id++) {
            if (id != 6) {
                debateRepository.save(new Debate(new UUID(0, id), "Another proposition " + id, "", format));
            }
        }
        debateRepository.save(debate);
        userPort.add(firstUserId, "alice");
        userPort.add(secondUserId, "bob");

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
                () -> assertEquals(2, result.content().size()),
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

    }

    @Test
    void shouldUseUnknownWhenParticipantUsernameIsMissing() {
        UUID userId = UUID.randomUUID();
        DebateFormat format = new DebateFormat(3, 60);

        Debate debate = new Debate(
                UUID.randomUUID(),
                "Moral and law are two separate matters",
                "A debate about moral and legal principles.",
                format
        );

        debate.addParticipant(DebateSide.FOR, userId);

        debateRepository.save(debate);

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
