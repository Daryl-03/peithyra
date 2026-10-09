package com.peithyra.api.identity.application;

import com.peithyra.api.identity.internal.adapters.persistence.InMemoryUserRepository;
import com.peithyra.api.identity.internal.application.port.in.GetUserInteractor;
import com.peithyra.api.identity.internal.domain.User;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class GetUserInteractorTest {
    private final InMemoryUserRepository repository = new InMemoryUserRepository();
    private final GetUserInteractor useCase = new GetUserInteractor(repository);
    @Test void returnsEmptyForUnknownIdentity() {
        assertTrue(useCase.execute("missing").isEmpty());
    }
    @Test void returnsMatchingProfile() {
        var user = new User(UUID.randomUUID(), "kinde-1", "Alice");
        repository.save(user);
        var view = useCase.execute("kinde-1").orElseThrow();
        assertEquals(user.getId(), view.id());
        assertEquals("Alice", view.username());
        assertEquals("kinde-1", view.externalId());
    }
}
