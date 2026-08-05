package com.peithyra.api.debate.E2E;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class DebateE2ETest {

    private WebTestClient webTestClient;

    @Test
    void returnCreatedDebateWithCreatedStatus(){
        String requestBody = """
                {
                    "proposition": "Test Debate",
                    "description": "This is a test debate",
                    "side": "FOR"
                }
                """;

        webTestClient.post()
                .uri("/debates")
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.proposition").isEqualTo("Test Debate")
                .jsonPath("$.description").isEqualTo("This is a test debate")
                .jsonPath("$.status").isEqualTo("WAITING_FOR_OPPONENT")
                .jsonPath("$.side").isEqualTo("FOR");

    }
}
