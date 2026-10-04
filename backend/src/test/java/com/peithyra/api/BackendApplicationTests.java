package com.peithyra.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@SpringBootTest
class BackendApplicationTests {

    @MockitoBean
    JwtDecoder jwtDecoder;

    @Test
    void contextLoads() {
    }

    @Test
    void createApplicationModuleModel() {
        ApplicationModules modules = ApplicationModules.of(BackendApplication.class);
        modules.forEach(System.out::println);
    }

    @Test
    void verifiesModularStructure() {
        ApplicationModules modules = ApplicationModules.of(BackendApplication.class);
        modules.verify();
    }
}
