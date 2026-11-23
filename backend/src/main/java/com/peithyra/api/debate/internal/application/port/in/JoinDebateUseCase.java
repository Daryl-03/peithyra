package com.peithyra.api.debate.internal.application.port.in;

import com.peithyra.api.debate.internal.application.exception.DebateAlreadyOngoingException;
import com.peithyra.api.debate.internal.application.exception.DebateNotFoundException;
import com.peithyra.api.debate.internal.application.dto.JoinDebateCommand;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.domain.DebateStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class JoinDebateUseCase {
    private final DebateRepository debateRepository;

    public JoinDebateUseCase(DebateRepository debateRepository) {
        this.debateRepository = debateRepository;
    }

    @Transactional
    public void execute(JoinDebateCommand command) {
        if (debateRepository.existsByParticipantIdAndStatusIn(command.userId(), Set.of(DebateStatus.WAITING_FOR_OPPONENT, DebateStatus.ONGOING, DebateStatus.WAITING_FOR_READY))) {
            throw new DebateAlreadyOngoingException("User already has a debate in progress");
        }

        var debate = debateRepository.findByIdForUpdate(command.debateId())
                .orElseThrow(() -> new DebateNotFoundException("Debate not found"));

        debate.addParticipant(command.side(), command.userId());
        debateRepository.save(debate);
    }
}
