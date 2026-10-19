package com.peithyra.api.identity.adapters;

import com.peithyra.api.identity.internal.adapters.persistence.InMemoryUserRepository;
import com.peithyra.api.identity.internal.domain.User;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryUserRepositoryTest {
    private final InMemoryUserRepository repository = new InMemoryUserRepository();

    @Test void returnsOnlyRequestedUsernamesAndOmitsUnknownIds() {
        var alice = new User(UUID.randomUUID(), "kinde-1", "Alice");
        var camille = new User(UUID.randomUUID(), "kinde-2", "Camille");
        repository.save(alice);
        repository.save(camille);
        repository.save(new User(UUID.randomUUID(), "kinde-3", "Thomas"));

        assertEquals(Map.of(alice.getId(), "Alice", camille.getId(), "Camille"),
                repository.findUsernamesByIds(Set.of(alice.getId(), camille.getId(), UUID.randomUUID())));
    }

    @Test void returnsEmptyWhenNoIdsAreKnown() {
        repository.save(new User(UUID.randomUUID(), "kinde-1", "Alice"));

        assertTrue(repository.findUsernamesByIds(Set.of(UUID.randomUUID())).isEmpty());
    }

    @Test void returnsEmptyForEmptyIdsEvenWhenUsersExist() {
        repository.save(new User(UUID.randomUUID(), "kinde-1", "Alice"));

        assertTrue(repository.findUsernamesByIds(Set.of()).isEmpty());
    }
}
