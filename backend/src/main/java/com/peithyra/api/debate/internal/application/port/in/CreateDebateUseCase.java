package com.peithyra.api.debate.internal.application.port.in;

import com.peithyra.api.debate.internal.application.dto.CreateDebateCommand;
import com.peithyra.api.debate.internal.application.dto.CreateDebateResponse;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.domain.Debate;
import com.peithyra.api.debate.internal.domain.DebateStatus;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateDebateUseCase {

    private final DebateRepository repository;

    public CreateDebateUseCase(DebateRepository repository) {
        this.repository = repository;
    }

    public CreateDebateResponse execute(CreateDebateCommand command) {
        Debate debate = new Debate(UUID.randomUUID(), command.proposition(), command.description());

        repository.save(debate);

        return new CreateDebateResponse(
                debate.getId(),
                debate.getProposition(),
                debate.getDescription(),
                DebateStatus.WAITING_FOR_OPPONENT,
                command.side(),
                debate.getCreatedAt()
        );
    }
}
