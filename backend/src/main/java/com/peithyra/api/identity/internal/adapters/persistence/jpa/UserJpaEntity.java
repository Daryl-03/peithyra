package com.peithyra.api.identity.internal.adapters.persistence.jpa;

import com.peithyra.api.identity.internal.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity {
    @Id
    private UUID id;
    private String username;
    private String externalId;
    private Instant createdAt;
    private Instant updatedAt;

    public UserJpaEntity(){}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserJpaEntity that = (UserJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public User toDomain() {
        return new User(
                id,
                username,
                externalId,
                createdAt,
                updatedAt
        );
    }

    public static UserJpaEntity fromDomain(User user){
        var entity = new UserJpaEntity();

        entity.id = user.getId();
        entity.username = user.getUsername();
        entity.externalId = user.getExternalId();
        entity.createdAt = user.getCreatedAt();
        entity.updatedAt = user.getUpdatedAt();

        return entity;
    }
}
