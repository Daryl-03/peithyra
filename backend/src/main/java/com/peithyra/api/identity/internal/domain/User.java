package com.peithyra.api.identity.internal.domain;

import java.time.Instant;
import java.util.UUID;

public class User {
    private final UUID id;
    private String username;
    private final String external_id;
    private final Instant createdAt;
    private final Instant updatedAt;

    public User(UUID id, String externalId) {
        this.id = id;
        external_id = externalId;
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    public User(UUID id, String external_id, String username) {
        this.id = id;
        this.external_id = external_id;
        setUsername(username);
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    public User(UUID id, String external_id, Instant createdAt, Instant updatedAt, String username) {
        this.id = id;
        this.external_id = external_id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        setUsername(username);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username.isBlank() || username.length() <= 4) {
            throw new IllegalArgumentException("Username should be at least 4 characters long");
        }
        this.username = username;
    }


    public UUID getId() {
        return id;
    }

    public String getExternal_id() {
        return external_id;
    }
}
