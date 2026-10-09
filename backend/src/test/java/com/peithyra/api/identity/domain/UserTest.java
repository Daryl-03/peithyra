package com.peithyra.api.identity.domain;

import com.peithyra.api.identity.internal.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test void acceptsFiveCharactersAndCanChangeUsername() {
        var user = new User(UUID.randomUUID(), "kinde-1", "Alice");
        assertEquals("Alice", user.getUsername());
        user.setUsername("Camille");
        assertEquals("Camille", user.getUsername());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @ParameterizedTest @ValueSource(strings = {"", "     ", "abc", "abcd"})
    void rejectsInvalidUsernameWithoutChangingExistingValue(String username) {
        var user = new User(UUID.randomUUID(), "kinde-1", "Alice");
        var error = assertThrows(IllegalArgumentException.class, () -> user.setUsername(username));
        assertEquals("Username should be at least 5 characters long", error.getMessage());
        assertEquals("Alice", user.getUsername());
        assertThrows(IllegalArgumentException.class, () -> new User(UUID.randomUUID(), "kinde-2", username));
    }
}
