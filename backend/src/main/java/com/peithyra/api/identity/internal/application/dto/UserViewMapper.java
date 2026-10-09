package com.peithyra.api.identity.internal.application.dto;

import com.peithyra.api.identity.UserView;
import com.peithyra.api.identity.internal.domain.User;

public class UserViewMapper {
    private UserViewMapper() {}

    public static UserView toView(User user) {
        return new UserView(
                user.getId(),
                user.getUsername(),
                user.getExternalId()
        );
    }
}
