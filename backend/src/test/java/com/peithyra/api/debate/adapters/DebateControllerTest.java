package com.peithyra.api.debate.adapters;

import com.peithyra.api.debate.internal.adapters.web.DebateController;
import com.peithyra.api.debate.internal.application.dto.CreateDebateCommand;
import com.peithyra.api.debate.internal.application.dto.CreateDebateResponse;
import com.peithyra.api.debate.internal.application.dto.ListDebateCommand;
import com.peithyra.api.debate.internal.application.dto.PagedResult;
import com.peithyra.api.debate.internal.application.port.in.CreateDebateUseCase;
import com.peithyra.api.debate.internal.application.port.in.ListDebatesUseCase;
import com.peithyra.api.debate.internal.domain.DebateSide;
import com.peithyra.api.debate.internal.domain.DebateStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DebateController.class)
public class DebateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateDebateUseCase createDebateUseCase;

    @MockitoBean
    private ListDebatesUseCase listDebatesUseCase;


    @Test
    void shouldReturnCreatedWhenDebateIsCreated() throws Exception {
        UUID creatorId = UUID.randomUUID();
        UUID debateId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        CreateDebateCommand expectedCommand = new CreateDebateCommand(
                "Moral and law are two separate matters",
                "This is about the relationship between moral and legal principles.",
                creatorId,
                DebateSide.FOR
        );

        when(createDebateUseCase.execute(expectedCommand))
                .thenReturn(new CreateDebateResponse(
                        debateId,
                        expectedCommand.proposition(),
                        expectedCommand.description(),
                        DebateStatus.WAITING_FOR_OPPONENT,
                        createdAt
                ));

        String requestBody = String.format("""
                {
                    "proposition": "%s",
                    "description": "%s",
                    "creatorId": "%s",
                    "side": "FOR"
                }
                """, expectedCommand.proposition(), expectedCommand.description(), creatorId);

        mockMvc.perform(post("/api/debates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(debateId.toString()))
                .andExpect(jsonPath("$.proposition")
                        .value(expectedCommand.proposition()))
                .andExpect(jsonPath("$.status")
                        .value("WAITING_FOR_OPPONENT"));

        verify(createDebateUseCase).execute(expectedCommand);
    }

    @Test
    void shouldReturnBadRequestWhenPropositionIsBlank() throws Exception {
        String requestBody = """
                {
                    "proposition": "",
                    "description": "This description is sufficiently long.",
                    "creatorId": "%s",
                    "side": "FOR"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/debates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createDebateUseCase);
    }

    @Test
    void shouldListDebatesUsingRequestedPagination() throws Exception {
        ListDebateCommand expectedCommand = new ListDebateCommand(2, 5);

        when(listDebatesUseCase.execute(expectedCommand))
                .thenReturn(new PagedResult<>(
                        List.of(),
                        2,
                        5,
                        0,
                        0
                ));

        mockMvc.perform(get("/api/debates")
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content").isArray());

        verify(listDebatesUseCase).execute(expectedCommand);
    }
}
