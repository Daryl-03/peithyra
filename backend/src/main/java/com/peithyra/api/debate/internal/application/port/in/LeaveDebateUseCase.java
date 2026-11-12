package com.peithyra.api.debate.internal.application.port.in;

import com.peithyra.api.debate.internal.application.exception.DebateNotFoundException;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LeaveDebateUseCase {
    private final DebateRepository debateRepository;

    public LeaveDebateUseCase(DebateRepository debateRepository) {
        this.debateRepository = debateRepository;
    }

    @Transactional
    public void execute(UUID debateId, UUID userId) {
        var debate = debateRepository.findByIdForUpdate(debateId)
                .orElseThrow(() -> new DebateNotFoundException("Debate not found"));

        if (debate.getCreatorId().equals(userId)) {
            debate.cancel(userId);
        } else {
            debate.removeParticipant(userId);
        }

        debateRepository.save(debate);
    }
}
