package com.peithyra.api.identity;

import java.util.Optional;

public interface GetUserUseCase {

    Optional<UserView> execute(String externalId);
}
