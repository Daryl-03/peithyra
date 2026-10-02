package com.peithyra.api.identity.internal.adapters.persistence;

import com.peithyra.api.identity.internal.application.port.out.UserRepository;
import com.peithyra.api.identity.internal.domain.User;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {
    private final ConcurrentHashMap<UUID, User> users = new ConcurrentHashMap<>();


    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public Optional<User> findByExternalId(String externalId) {
        return users.values().stream()
                .filter(user -> user.getExternalId().equals(externalId) )
                .findFirst();
    }
}
