package com.peithyra.api.identity.application;

import com.peithyra.api.identity.internal.application.port.in.GetUsernamesInteractor;
import com.peithyra.api.identity.internal.application.port.out.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GetUsernamesInteractorTest {
    private final UserRepository repository = mock(UserRepository.class);
    private final GetUsernamesInteractor useCase = new GetUsernamesInteractor(repository);

    @Test void returnsUsernamesForKnownIdsAndOmitsUnknownIds() {
        var aliceId = UUID.randomUUID();
        var camilleId = UUID.randomUUID();
        var ids = Set.of(aliceId, camilleId, UUID.randomUUID());
        var usernames = Map.of(aliceId, "Alice", camilleId, "Camille");
        when(repository.findUsernamesByIds(ids)).thenReturn(usernames);

        assertEquals(usernames, useCase.execute(ids));
        verify(repository).findUsernamesByIds(ids);
        verifyNoMoreInteractions(repository);
    }

    @Test void returnsEmptyWhenNoIdsAreKnown() {
        var ids = Set.of(UUID.randomUUID());
        when(repository.findUsernamesByIds(ids)).thenReturn(Map.of());

        assertTrue(useCase.execute(ids).isEmpty());
    }

    @Test void returnsEmptyWithoutQueryingRepositoryForEmptyIds() {
        assertTrue(useCase.execute(Set.of()).isEmpty());
        verifyNoInteractions(repository);
    }

    @Test void returnsEmptyWithoutQueryingRepositoryForNullIds() {
        assertTrue(useCase.execute(null).isEmpty());
        verifyNoInteractions(repository);
    }
}
