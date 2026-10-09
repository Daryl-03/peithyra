package com.peithyra.api.identity.internal.adapters.persistence.jpa;

import com.peithyra.api.identity.internal.application.port.out.UserRepository;
import com.peithyra.api.identity.internal.domain.User;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataIntegrityViolationException;
import org.hibernate.exception.ConstraintViolationException;
import com.peithyra.api.identity.internal.application.port.out.ProfileConflictException;

import java.util.Optional;

@Repository
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository repository;

    public JpaUserRepository(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(User user) {
        try {
            repository.saveAndFlush(UserJpaEntity.fromDomain(user));
        } catch (DataIntegrityViolationException ex) {
            for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
                if (cause instanceof ConstraintViolationException constraint
                        && ("users_username_unique".equals(constraint.getConstraintName())
                        || "users_external_id_key".equals(constraint.getConstraintName()))) {
                    throw new ProfileConflictException();
                }
            }
            throw ex;
        }
    }

    @Override
    public boolean existsByUsernameIgnoreCase(String username) {
        return repository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public Optional<User> findByExternalId(String externalId) {
        return repository.findByExternalId(externalId).map(UserJpaEntity::toDomain);
    }
}
