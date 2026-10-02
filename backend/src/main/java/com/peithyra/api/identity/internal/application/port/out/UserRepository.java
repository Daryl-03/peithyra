package com.peithyra.api.identity.internal.application.port.out;


import com.peithyra.api.identity.internal.domain.User;

import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findByExternalId(String externalId);
}
