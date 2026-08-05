package com.peithyra.api.debate.E2E.debate.adapters.web;

import com.peithyra.api.debate.internal.adapters.web.DebateController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
public class DebateControllerTest {

    private MockMvc mockMvc;

    @Test
    void shouldReturn201AndCreatedDebateWhenRequestIsValid() {
        String requestBody = """
                {
                    "proposition": "Test Debate",
                    "description": "This is a test debate",
                    "side": "FOR"
                }
                """;
    }
}
