package com.peithyra.api.debate.internal.application.port.in;

import com.peithyra.api.debate.internal.application.exception.DebateAlreadyOngoingException;
import com.peithyra.api.debate.internal.application.exception.DebateNotFoundException;
import com.peithyra.api.debate.internal.application.exception.DebateNotJoinableException;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.domain.DebateSide;
import com.peithyra.api.debate.internal.domain.DebateStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class JoinDebateUseCase {
    private final DebateRepository debateRepository;

    public JoinDebateUseCase(DebateRepository debateRepository) {
        this.debateRepository = debateRepository;
    }

    @Transactional
    public void execute(UUID debateId, UUID userId) {
        if (debateRepository.existsByParticipantIdAndStatusIn(userId, Set.of(DebateStatus.WAITING_FOR_OPPONENT, DebateStatus.ONGOING, DebateStatus.WAITING_FOR_READY))) {
            throw new DebateAlreadyOngoingException("User already has a debate in progress");
        }

        var debate = debateRepository.findByIdForUpdate(debateId)
                .orElseThrow(() -> new DebateNotFoundException("Debate not found"));

        if(debate.getParticipants().size() >= 2 || debate.getStatus() != DebateStatus.WAITING_FOR_OPPONENT) {
            throw new DebateNotJoinableException("Debate is not available for joining");
        }

        var participantSide = debate.getParticipants().getFirst().side() == DebateSide.AGAINST ? DebateSide.FOR : DebateSide.AGAINST;
        debate.addParticipant(participantSide, userId);
        debateRepository.save(debate);
    }
}
