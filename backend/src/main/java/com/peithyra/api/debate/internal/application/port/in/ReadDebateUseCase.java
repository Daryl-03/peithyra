package com.peithyra.api.debate.internal.application.port.in;

import com.peithyra.api.debate.internal.application.dto.DebateDetails;
import com.peithyra.api.debate.internal.application.dto.ParticipantSummary;
import com.peithyra.api.debate.internal.application.exception.DebateNotFoundException;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.application.port.out.UserPort;
import com.peithyra.api.debate.internal.domain.Participant;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class ReadDebateUseCase {
    private final DebateRepository debateRepository;
    private final UserPort userPort;

    public ReadDebateUseCase(DebateRepository debateRepository, UserPort userPort) {
        this.debateRepository = debateRepository;
        this.userPort = userPort;
    }

    public DebateDetails execute(UUID debateId) {
        var debate = debateRepository.findById(debateId)
                .orElseThrow(() -> new DebateNotFoundException("Debate not found"));

        var participantsIds = debate.getParticipants().stream()
                .map(Participant::userId)
                .toList();

        var participantUsernames = userPort.findUsernamesByIds(Set.copyOf(participantsIds));

        return new DebateDetails(
                debate.getId(),
                debate.getProposition(),
                debate.getDescription(),
                debate.getStatus(),
                debate.getFormat(),
                debate.getCreatedAt(),
                debate.getParticipants().stream()
                        .map(participant -> new ParticipantSummary(
                                participant.userId(),
                                participantUsernames.getOrDefault(participant.userId(), "unknown"),
                                participant.side()
                        ))
                        .toList()
        );
    }
}
