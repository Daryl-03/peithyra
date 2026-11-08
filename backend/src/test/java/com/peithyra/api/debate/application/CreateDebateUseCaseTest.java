package com.peithyra.api.debate.application;

import com.peithyra.api.debate.support.InMemoryDebateRepository;
import com.peithyra.api.debate.internal.application.dto.CreateDebateCommand;

import com.peithyra.api.debate.internal.application.exception.DebateAlreadyOngoingException;
import com.peithyra.api.debate.internal.application.port.in.CreateDebateUseCase;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;

import com.peithyra.api.debate.internal.domain.DebateFormat;
import com.peithyra.api.debate.internal.domain.DebateSide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.peithyra.api.debate.internal.domain.Debate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class CreateDebateUseCaseTest {

    private CreateDebateUseCase createDebateUseCase;

    private DebateRepository repository;

    @BeforeEach
    void setup() {
        repository = new InMemoryDebateRepository();
        createDebateUseCase = new CreateDebateUseCase(repository);
    }

    @Test
    void shouldCreateParticipantWithDebate() {
        UUID creatorId = UUID.randomUUID();
        DebateFormat format = new DebateFormat(3, 60);
        var result = createDebateUseCase.execute(
                new CreateDebateCommand(
                        "We should switch to electric cars",
                        "",
                        creatorId,
                        DebateSide.FOR,
                        format
                )
        );

        Debate debate = repository.findById(result.id()).orElseThrow();

        assertAll(
                () -> assertEquals(1, debate.getParticipants().size()),
                () -> assertEquals(creatorId, debate.getParticipants().getFirst().userId()),
                () -> assertEquals(format, debate.getFormat())
        );
    }

    @Test
    void shouldNotCreateDebateIfCreatorHasExistingDebate() {
        UUID creatorId = UUID.randomUUID();
        DebateFormat format = new DebateFormat(3, 60);
        Debate existingDebate = new Debate(
                UUID.randomUUID(),
                "An existing debate",
                "",
                format
        );
        existingDebate.addParticipant(DebateSide.FOR, creatorId);
        repository.save(existingDebate);

        assertThrows(DebateAlreadyOngoingException.class, () -> {
            createDebateUseCase.execute(
                    new CreateDebateCommand(
                            "We should switch to electric cars",
                            "",
                            creatorId,
                            DebateSide.FOR,
                            format
                    )
            );
        });
    }
}
