package com.peithyra.api.identity.internal.adapters.persistence;

import com.peithyra.api.identity.internal.application.port.out.UserRepository;
import com.peithyra.api.identity.internal.domain.User;

import java.util.Optional;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {
    private final ConcurrentHashMap<UUID, User> users = new ConcurrentHashMap<>();

    @Override
    public Map<UUID, String> findUsernamesByIds(Set<UUID> userIds) {
        return users.values().stream()
                .filter(user -> userIds.contains(user.getId()))
                .collect(Collectors.toMap(User::getId, User::getUsername));
    }


    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public boolean existsByUsernameIgnoreCase(String username) {
        return users.values().stream().anyMatch(user -> username.equalsIgnoreCase(user.getUsername()));
    }

    @Override
    public Optional<User> findByExternalId(String externalId) {
        return users.values().stream()
                .filter(user -> user.getExternalId().equals(externalId) )
                .findFirst();
    }
}
