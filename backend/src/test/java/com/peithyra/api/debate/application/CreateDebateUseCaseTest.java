package com.peithyra.api.debate.application;

import com.peithyra.api.debate.internal.application.dto.CreateDebateCommand;

import com.peithyra.api.debate.internal.application.port.in.CreateDebateUseCase;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;

import com.peithyra.api.debate.internal.domain.DebateSide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.peithyra.api.debate.internal.domain.Debate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class CreateDebateUseCaseTest {

    private CreateDebateUseCase createDebateUseCase;

    private DebateRepository repository;

    @BeforeEach
    void setup() {
        repository = mock(DebateRepository.class);
        createDebateUseCase = new CreateDebateUseCase(repository);
    }

    @Test
    void shouldCreateParticipantWithDebate() {
        UUID creatorId = UUID.randomUUID();
        createDebateUseCase.execute(
                new CreateDebateCommand(
                        "We should switch to electric cars",
                        "",
                        creatorId,
                        DebateSide.FOR
                )
        );

        ArgumentCaptor<Debate> captor = ArgumentCaptor.forClass(Debate.class);
        verify(repository).save(captor.capture());

        Debate debate = captor.getValue();

        assertAll(
                () -> assertEquals(1, debate.getParticipants().size()),
                () -> assertEquals(creatorId, debate.getParticipants().getFirst().userId())
        );
    }
}
