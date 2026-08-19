package com.peithyra.api.debate.internal.application.port.in;

import com.peithyra.api.debate.internal.application.dto.DebateSummary;
import com.peithyra.api.debate.internal.application.dto.ListDebateCommand;
import com.peithyra.api.debate.internal.application.dto.PagedResult;
import com.peithyra.api.debate.internal.application.dto.ParticipantSummary;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.application.port.out.UserPort;
import com.peithyra.api.debate.internal.domain.Debate;
import com.peithyra.api.debate.internal.domain.Participant;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ListDebatesUseCase {
    private final DebateRepository debateRepository;
    private final UserPort userPort;

    public ListDebatesUseCase(DebateRepository debateRepository, UserPort userPort) {
        this.debateRepository = debateRepository;
        this.userPort = userPort;
    }

    public PagedResult<DebateSummary> execute(ListDebateCommand command) {
        PagedResult<Debate> debatesPage = debateRepository.findDebatesPage(command.page(), command.size());

        var participantsIds = debatesPage.content().stream()
                .flatMap(
                        debate -> debate.getParticipants().stream()
                                .map(Participant::userId)
                )
                .collect(Collectors.toSet());

        Map<UUID, String> participantUsernames = userPort.findUsernamesByIds(participantsIds);

        return new PagedResult<>(
                debatesPage.content().stream()
                        .map(debate -> new DebateSummary(
                                debate.getId(),
                                debate.getProposition(),
                                debate.getStatus(),
                                debate.getCreatedAt(),
                                debate.getParticipants().stream()
                                        .map(participant -> new ParticipantSummary(
                                                participant.userId(),
                                                participantUsernames.getOrDefault(participant.userId(), "unknown"),
                                                participant.side()
                                        ))
                                        .collect(Collectors.toList())
                        ))
                        .collect(Collectors.toList()),
                debatesPage.page(),
                debatesPage.size(),
                debatesPage.totalElements(),
                debatesPage.totalPages()
        );
    }
}
