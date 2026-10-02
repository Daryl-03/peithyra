package com.peithyra.api.identity.internal.adapters.persistence.jpa;

import com.peithyra.api.identity.internal.application.port.out.UserRepository;
import com.peithyra.api.identity.internal.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository repository;

    public JpaUserRepository(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(User user) {
        repository.save(user);
    }

    @Override
    public Optional<User> findByExternalId(String externalId) {
        return repository.findByExternalId(externalId);
    }
}
