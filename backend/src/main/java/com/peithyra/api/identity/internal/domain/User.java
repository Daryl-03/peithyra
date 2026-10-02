package com.peithyra.api.identity.internal.domain;

import java.time.Instant;
import java.util.UUID;

public class User {
    private final UUID id;
    private String username;
    private final String externalId;
    private final Instant createdAt;
    private final Instant updatedAt;

    public User(UUID id, String externalId) {
        this.id = id;
        this.externalId = externalId;
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    public User(UUID id, String externalId, String username) {
        this.id = id;
        this.externalId = externalId;
        setUsername(username);
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    public User(UUID id, String username, String externalId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.externalId = externalId;
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

    public String getExternalId() {
        return externalId;
    }
}
