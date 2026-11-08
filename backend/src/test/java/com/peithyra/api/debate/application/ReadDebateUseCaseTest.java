package com.peithyra.api.debate.application;


import com.peithyra.api.debate.internal.application.dto.DebateDetails;
import com.peithyra.api.debate.internal.application.exception.DebateNotFoundException;
import com.peithyra.api.debate.support.InMemoryDebateRepository;
import com.peithyra.api.debate.internal.application.port.in.ReadDebateUseCase;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.domain.Debate;
import com.peithyra.api.debate.internal.domain.DebateFormat;
import com.peithyra.api.debate.internal.domain.DebateSide;
import com.peithyra.api.debate.support.InMemoryUserAdapter;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReadDebateUseCaseTest {
    private final ReadDebateUseCase readDebateUseCase;
    private final DebateRepository debateRepository;
    private final InMemoryUserAdapter userPort;


    public ReadDebateUseCaseTest() {
        debateRepository = new InMemoryDebateRepository();
        userPort = new InMemoryUserAdapter();
        readDebateUseCase = new ReadDebateUseCase(debateRepository, userPort);
    }

    @Test
    void shouldReadExistingDebate() {
        // Given
        UUID creatorId = UUID.randomUUID();
        Debate debate = new Debate(
                creatorId,
                "Moral and law are two separate matters",
                "A debate about moral and legal principles.",
                new DebateFormat(3, 60)
        );
        debate.addParticipant(DebateSide.FOR, creatorId);
        debateRepository.save(debate);
        userPort.add(creatorId, "alice");

        // When
        DebateDetails result = readDebateUseCase.execute(debate.getId());

        // Then
        assertEquals(debate.getId(), result.id());
        assertEquals(debate.getProposition(), result.proposition());
        assertEquals(debate.getDescription(), result.description());
        assertEquals(debate.getStatus(), result.status());

        assertEquals(1, result.participants().size());
        assertEquals(creatorId, result.participants().getFirst().id());
        assertEquals("alice", result.participants().getFirst().username());
    }

    @Test
    void shouldThrowExceptionForNonExistingDebate() {
        // Given
        UUID nonExistingDebateId = UUID.randomUUID();

        // When & Then
        assertThrows(
                DebateNotFoundException.class,
                () -> readDebateUseCase.execute(nonExistingDebateId)
        );
    }

}
