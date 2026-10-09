package com.peithyra.api.identity.application;

import com.peithyra.api.identity.internal.adapters.persistence.InMemoryUserRepository;
import com.peithyra.api.identity.internal.application.port.in.CreateUserUseCase;
import com.peithyra.api.identity.internal.application.port.out.ProfileConflictException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreateUserUseCaseTest {
    private final InMemoryUserRepository repository = new InMemoryUserRepository();
    private final CreateUserUseCase useCase = new CreateUserUseCase(repository);

    @Test void savesProfileAndReturnsItsView() {
        var view = useCase.execute("Alice", "kinde-1");
        var saved = repository.findByExternalId("kinde-1").orElseThrow();
        assertNotNull(view.id());
        assertEquals(saved.getId(), view.id());
        assertEquals("Alice", view.username());
        assertEquals("kinde-1", view.externalId());
    }
    @Test void doesNotSaveInvalidUsername() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute("abcd", "kinde-1"));
        assertTrue(repository.findByExternalId("kinde-1").isEmpty());
    }
    @Test void rejectsExistingIdentityWithoutReplacingItsProfile() {
        var original = useCase.execute("Alice", "kinde-1");
        assertThrows(ProfileConflictException.class, () -> useCase.execute("Camille", "kinde-1"));
        assertEquals(original.id(), repository.findByExternalId("kinde-1").orElseThrow().getId());
        assertEquals("Alice", repository.findByExternalId("kinde-1").orElseThrow().getUsername());
    }
    @Test void rejectsUsernameIgnoringCase() {
        useCase.execute("Alice", "kinde-1");
        assertThrows(ProfileConflictException.class, () -> useCase.execute("ALICE", "kinde-2"));
        assertTrue(repository.findByExternalId("kinde-2").isEmpty());
    }
}
