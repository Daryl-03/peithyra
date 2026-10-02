package com.peithyra.api.identity.internal.application.dto;

import com.peithyra.api.identity.internal.domain.User;

import java.util.UUID;

public record UserView(
        UUID id,
        String username,
        String external_id
) {

    public UserView fromDomain(User user){
        return new UserView(user.getId(), user.getUsername(), user.getExternalId());
    }
}
