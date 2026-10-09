package com.peithyra.api.identity.adapters;

import com.peithyra.api.TestcontainersConfiguration;
import com.peithyra.api.identity.internal.adapters.persistence.jpa.SpringDataUserRepository;
import com.peithyra.api.identity.internal.application.port.out.UserRepository;
import com.peithyra.api.identity.internal.application.port.out.ProfileConflictException;
import com.peithyra.api.identity.internal.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JpaUserRepositoryIntegrationTest {
    @Autowired UserRepository repository;
    @Autowired SpringDataUserRepository springRepository;
    @BeforeEach void clean() { springRepository.deleteAll(); }

    @Test void savesAndRestoresProfileAndDates() {
        var created = Instant.parse("2026-01-01T12:00:00Z");
        var updated = created.plusSeconds(60);
        var user = new User(UUID.randomUUID(), "Alice", "kinde-1", created, updated);
        repository.save(user);
        var result = repository.findByExternalId("kinde-1").orElseThrow();
        assertEquals(user.getId(), result.getId());
        assertEquals("Alice", result.getUsername());
        assertEquals("kinde-1", result.getExternalId());
        assertEquals(created, result.getCreatedAt());
        assertEquals(updated, result.getUpdatedAt());
        assertTrue(repository.existsByUsernameIgnoreCase("ALICE"));
        assertFalse(repository.existsByUsernameIgnoreCase("Camille"));
        assertTrue(repository.findByExternalId("missing").isEmpty());
    }
    @Test void databaseRejectsDuplicateUsernameIgnoringCaseWithoutPrecheck() {
        repository.save(new User(UUID.randomUUID(), "kinde-1", "Alice"));
        assertThrows(ProfileConflictException.class,
                () -> repository.save(new User(UUID.randomUUID(), "kinde-2", "ALICE")));
        assertTrue(repository.findByExternalId("kinde-2").isEmpty());
    }
    @Test void databaseRejectsDuplicateIdentityWithoutPrecheck() {
        repository.save(new User(UUID.randomUUID(), "kinde-1", "Alice"));
        assertThrows(ProfileConflictException.class,
                () -> repository.save(new User(UUID.randomUUID(), "kinde-1", "Camille")));
        assertEquals("Alice", repository.findByExternalId("kinde-1").orElseThrow().getUsername());
    }
    @Test void unrelatedIntegrityErrorsAreNotReportedAsConflicts() {
        assertThrows(DataIntegrityViolationException.class,
                () -> repository.save(new User(UUID.randomUUID(), null, "Alice")));
    }
}
