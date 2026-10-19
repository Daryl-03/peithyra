package com.peithyra.api.debate.internal.application.port.in;

import com.peithyra.api.debate.internal.application.dto.CreateDebateCommand;
import com.peithyra.api.debate.internal.application.dto.CreateDebateResponse;
import com.peithyra.api.debate.internal.application.exceptioin.DebateAlreadyOngoingException;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.domain.Debate;
import com.peithyra.api.debate.internal.domain.DebateStatus;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class CreateDebateUseCase {

    private final DebateRepository repository;

    public CreateDebateUseCase(DebateRepository repository) {
        this.repository = repository;
    }

    public CreateDebateResponse execute(CreateDebateCommand command) {

        if (repository.existsByCreatorIdAndStatusIn(command.creatorId(), Set.of(DebateStatus.WAITING_FOR_OPPONENT, DebateStatus.ONGOING))) {
            throw new DebateAlreadyOngoingException("User already has a debate in progress");
        }

        Debate debate = new Debate(UUID.randomUUID(), command.proposition(), command.description(), command.format());

        debate.addParticipant(
                        command.side(),
                        command.creatorId()
        );

        repository.save(debate);

        return new CreateDebateResponse(
                debate.getId(),
                debate.getProposition(),
                debate.getDescription(),
                DebateStatus.WAITING_FOR_OPPONENT,
                debate.getCreatedAt(),
                debate.getFormat()
        );
    }
}
