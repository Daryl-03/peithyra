package com.peithyra.api.debate.adapters;

import com.peithyra.api.TestcontainersConfiguration;
import com.peithyra.api.debate.internal.adapters.persistence.jpa.SpringDataDebateRepository;
import com.peithyra.api.debate.internal.application.dto.PagedResult;
import com.peithyra.api.debate.internal.application.port.out.DebateRepository;
import com.peithyra.api.debate.internal.domain.Debate;
import com.peithyra.api.debate.internal.domain.DebateSide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JpaDebateRepositoryIntegrationTest {

    @Autowired
    private DebateRepository debateRepository;

    @Autowired
    private SpringDataDebateRepository springDataDebateRepository;

    @BeforeEach
    public void clearDatabase() {
        springDataDebateRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveDebateWithParticipants() {
        UUID debateId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();

        Debate debate = new Debate(
                debateId,
                "Moral and law are two separate matters",
                "A debate about moral and legal principles."
        );

        debate.addParticipant(DebateSide.FOR, creatorId);

        debateRepository.save(debate);

        PagedResult<Debate> result = debateRepository.findDebatesPage(0, 10);
        Debate retrievedDebate = result.content().getFirst();
        assertAll(
                () -> assertEquals(1, result.totalElements()),
                () -> assertEquals(1, result.totalPages()),
                () -> assertEquals(1, result.content().size()),
                () -> assertEquals(debateId, retrievedDebate.getId()),
                () -> assertEquals(
                        creatorId,
                        retrievedDebate.getParticipants()
                                .getFirst()
                                .userId()
                )
        );
    }
}
