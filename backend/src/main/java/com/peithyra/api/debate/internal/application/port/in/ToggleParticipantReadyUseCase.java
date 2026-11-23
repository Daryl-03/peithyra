package com.peithyra.api.debate.internal.application.port.in;

import com.peithyra.api.debate.internal.application.exception.DebateNotFoundException;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ToggleParticipantReadyUseCase {
    private final DebateRepository debateRepository;

    public ToggleParticipantReadyUseCase(DebateRepository debateRepository) {
        this.debateRepository = debateRepository;
    }

    @Transactional
    public void execute(UUID debateId, UUID userId) {
        var debate = debateRepository.findByIdForUpdate(debateId)
                .orElseThrow(() -> new DebateNotFoundException("Debate not found"));
        debate.toggleParticipantReady(userId);
        debateRepository.save(debate);
    }
}
