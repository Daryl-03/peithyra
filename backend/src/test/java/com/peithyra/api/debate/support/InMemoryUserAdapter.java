package com.peithyra.api.debate.support;

import com.peithyra.api.debate.internal.application.port.out.UserPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class InMemoryUserAdapter implements UserPort {
    private final Map<UUID, String> usernames = new HashMap<>();

    public void add(UUID userId, String username) {
        usernames.put(userId, username);
    }

    @Override
    public Map<UUID, String> findUsernamesByIds(Set<UUID> userIds) {
        Map<UUID, String> result = new HashMap<>();
        for (UUID userId : userIds) {
            if (usernames.containsKey(userId)) {
                result.put(userId, usernames.get(userId));
            }
        }
        return result;
    }
}
