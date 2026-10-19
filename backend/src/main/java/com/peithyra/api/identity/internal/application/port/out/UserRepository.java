package com.peithyra.api.identity.internal.application.port.out;


import com.peithyra.api.identity.internal.domain.User;

import java.util.Optional;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface UserRepository {
    void save(User user);
    boolean existsByUsernameIgnoreCase(String username);
    Optional<User> findByExternalId(String externalId);
    Map<UUID, String> findUsernamesByIds(Set<UUID> userIds);
}
